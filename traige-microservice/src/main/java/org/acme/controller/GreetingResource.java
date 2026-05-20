package org.acme.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.*;

import java.util.Map;


@ApplicationScoped
@Path("/hello")
public class GreetingResource {
    @Inject
    BedrockService bedrockService;
    @Inject
    TriageModelTrainer trainer;

    @POST
    @Path("/triage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response triageRequest(TriageInput input) {
        return bedrockService.llmJsonParser(input);
    }

    @POST
    @Path("/parse-symptoms")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response parseSymptoms(TriageInput input) {
        return bedrockService.llmJsonParser(input);
    }


    @POST
    @Path("/train")
    @Produces(MediaType.TEXT_PLAIN)
    public Response trainModel() {
        try {
            trainer.trainOrLoadModel("src/main/resources/Modeling_Dataset_with_Encoded_Triage_and_Conditions.csv");
            return Response.ok("Model trained or loaded successfully").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Training failed: " + e.getMessage()).build();
        }
    }

    @POST
    @Path("/predict")
    public Response predict(PredictionRequest request) {
        if (!trainer.isReady()) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("Model not loaded").build();
        }

        int prediction = trainer.predict(request.features);
        double[] probs = trainer.predictWithProbabilities(request.features);
        return Response.ok(new PredictionResponse(prediction, probs)).build();
    }

    @POST
    @Path("/parse-and-predict")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response parseAndPredict(TriageInput input) {
        try {
            Response response = bedrockService.llmJsonParser(input);
            ParsedTriageData parsed = response.readEntity(ParsedTriageData.class);
            double[] fullVector = bedrockService.buildFullFeatureVector(parsed);
            int predicted = trainer.predict(fullVector);
            double[] probs = trainer.predictWithProbabilities(fullVector);
            return Response.ok(new PredictionResponse(predicted, probs)).build();
        } catch (Exception e) {
            return Response.serverError().entity("Error: " + e.getMessage()).build();
        }
    }


    @Inject
    TriageBayesianNetworkService bayesianService;

    @POST
    @Path("/bayes-predict")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response predictWithBayesianNet(TriageInput input) throws JsonProcessingException {
        Response response = bedrockService.llmJsonParserBayes(input);
        BayesianRequest parsed = response.readEntity(BayesianRequest.class);
        Response bayesianResponse = bedrockService.predictWithBayesian(parsed);
        BayesianPredictionResponse bayesianPredictionResponse = bayesianResponse.readEntity(BayesianPredictionResponse.class);
        return Response.ok(bayesianPredictionResponse).build();
    }

    @POST
    @Path("/combined_traige")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response combinedTriage(TriageInput input) throws Exception {
        trainer.trainOrLoadModel("src/main/resources/Modeling_Dataset_with_Encoded_Triage_and_Conditions.csv");
        if (!trainer.isReady()) {
            trainer.trainOrLoadModel("src/main/resources/Modeling_Dataset_with_Encoded_Triage_and_Conditions.csv");
        }

        FinalTriageRequest triageRequest = new FinalTriageRequest();

        Response response = bedrockService.llmJsonParser(input);
        ParsedTriageData parsedLN = response.readEntity(ParsedTriageData.class);
        double[] fullVector = bedrockService.buildFullFeatureVector(parsedLN);
        int predicted = trainer.predict(fullVector);
        double[] probs = trainer.predictWithProbabilities(fullVector);

        FinalTriageRequest.LogisticRegressionTriageResult logisticRegressionTriageResult = new FinalTriageRequest.LogisticRegressionTriageResult();
        logisticRegressionTriageResult.setFinalLogisticTriage(String.valueOf(predicted));
        logisticRegressionTriageResult.setLowLRTriageProbabllity(String.valueOf(probs[0]));
        logisticRegressionTriageResult.setMediumLRTriageProbabllity(String.valueOf(probs[1]));
        logisticRegressionTriageResult.setUrgentLRTriageProbabllity(String.valueOf(probs[2]));



        Response response2 = bedrockService.llmJsonParserBayes(input);
        BayesianRequest parsedBN = response2.readEntity(BayesianRequest.class);
        Response bayesianResponse = bedrockService.predictWithBayesian(parsedBN);
        BayesianPredictionResponse bayesianPredictionResponse = bayesianResponse.readEntity(BayesianPredictionResponse.class);

        FinalTriageRequest.BayesianNetTriageResult bayesianNetTriageResult = new FinalTriageRequest.BayesianNetTriageResult();
        bayesianNetTriageResult.setFinalBayesianNetTriage(bayesianPredictionResponse.getPredictedTriageLevel());
        bayesianNetTriageResult.setLowBNTriageProbabllity(bayesianPredictionResponse.getTriageLevelProbabilities().get("lowTriageProb").toString());
        bayesianNetTriageResult.setMediumBNTriageProbabllity(bayesianPredictionResponse.getTriageLevelProbabilities().get("medTriageProb").toString());
        bayesianNetTriageResult.setUrgentBNTriageProbabllity(bayesianPredictionResponse.getTriageLevelProbabilities().get("UrgentTriageProb").toString());

        triageRequest.setLogisticRegressionResult(logisticRegressionTriageResult);
        triageRequest.setBayesianNetResult(bayesianNetTriageResult);

         bedrockService.forwardFeedToRag(triageRequest,input);


        return  bedrockService.forwardFeedToRag(triageRequest,input);
    }


}
