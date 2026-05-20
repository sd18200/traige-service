package org.acme;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagentruntime.BedrockAgentRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockagentruntime.model.*;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import static org.msdsolutions.common.CommonUtils.requestClearer;


@ApplicationScoped
public class BedrockService {
    public static final String END_MODEL_CARD = "<|eot_id|><|start_header_id|>assistant<|end_header_id|>";

    @RestClient
    BayesianRestClient bayesianRestClient;

    public static final List<String> knownConditionsBayes = List.of(
            "has_hypertension",
            "has_stroke",
            "has_sepsis_disorder",
            "has_pneumonia",
            "has_dyspnea_finding",
            "has_diabetes",
            "has_myocardial_infarction",
            "has_chronic_congestive_heart_failure_disorder",
            "has_fever_finding",
            "has_cardiac_arrest",
            "has_history_of_cardiac_arrest_situation",
            "has_history_of_myocardial_infarction_situation",
            "has_stress_finding",
            "has_not_in_labor_force_finding",
            "has_full-time_employment_finding",
            "has_victim_of_intimate_partner_abuse_finding",
            "has_part-time_employment_finding",
            "has_coronary_heart_disease",
            "has_limited_social_contact_finding",
            "has_hypoxemia_disorder",
            "has_pneumonia_disorder",
            "has_respiratory_distress_finding",
            "has_social_isolation_finding",
            "has_childhood_asthma",
            "has_reports_of_violence_in_the_environment_finding",
            "has_has_a_criminal_record_finding",
            "has_diabetic_renal_disease_disorder",
            "has_prediabetes",
            "has_chronic_kidney_disease_stage_1_disorder"
    );


    @Inject
    static ApplicationPropertiesCredentialsProvider credentialsProvider;



    private static BedrockAgentRuntimeAsyncClient asyncClient;

    public static synchronized BedrockAgentRuntimeAsyncClient getAsyncClient() {
        if (asyncClient == null) {
            asyncClient = BedrockAgentRuntimeAsyncClient.builder()
                    .credentialsProvider(credentialsProvider)
                    .region(Region.EU_WEST_2)
                    .httpClientBuilder(
                            NettyNioAsyncHttpClient.builder()
                                    .maxConcurrency(300)
                                    .maxPendingConnectionAcquires(10000)
                                    .connectionAcquisitionTimeout(Duration.ofSeconds(30))
                    )
                    .build();
        }
        return asyncClient;
    }

    private static BedrockRuntimeAsyncClient asyncClient2;

    public static synchronized BedrockRuntimeAsyncClient getAsyncClient2() {
        if (asyncClient2 == null) {
            asyncClient2 = BedrockRuntimeAsyncClient.builder()
                    .region(Region.EU_WEST_2)
                    .credentialsProvider(credentialsProvider)
                    .httpClientBuilder(
                            NettyNioAsyncHttpClient.builder()
                                    .maxConcurrency(100)
                                    .maxPendingConnectionAcquires(10000)
                                    .connectionTimeout(Duration.ofSeconds(10 * 30))
                                    .readTimeout(Duration.ofSeconds(30 * 10))
                    )
                    .build();
        }
        return asyncClient2;
    }

    public static synchronized BedrockAgentRuntimeAsyncClient getAsyncClient3() {
        if (asyncClient == null) {
            asyncClient = BedrockAgentRuntimeAsyncClient.builder()
                    .credentialsProvider(credentialsProvider)
                    .region(Region.EU_WEST_2)
                    .httpClientBuilder(
                            NettyNioAsyncHttpClient.builder()
                                    .maxConcurrency(300) // 🔥 Increase concurrency
                                    .maxPendingConnectionAcquires(10000)
                                    .connectionTimeout(Duration.ofSeconds(300)) // 5 min
                                    .readTimeout(Duration.ofSeconds(300)) // 5 min
                                    .connectionAcquisitionTimeout(Duration.ofSeconds(60)) // 🔥 Give time for slow model
                    )
                    .build();
        }
        return asyncClient;
    }



    public RetrieveAndGenerateRequest ragTriageTestRequest(TriageInput userMessage) {
        System.out.println("Started Request body creation");
        KnowledgeBaseRetrieveAndGenerateConfiguration knowledgeBaseConfig =
                KnowledgeBaseRetrieveAndGenerateConfiguration.builder()
                        .knowledgeBaseId("90620YHRKV")
                        .modelArn("mistral.mistral-large-2402-v1:0")
                        .orchestrationConfiguration(
                                OrchestrationConfiguration.builder()
                                        .inferenceConfig(InferenceConfig
                                                .builder()
                                                .textInferenceConfig(TextInferenceConfig.builder()
                                                        .maxTokens(2048)
                                                        .temperature(0.7F)
                                                        .topP(0.7F)
                                                        .build())
                                                .build())
                                        .promptTemplate(PromptTemplate.builder().textPromptTemplate(ORCHFULLTRIAGE).build())
                                        .build())
                        .generationConfiguration(GenerationConfiguration.builder()
                                .inferenceConfig(InferenceConfig
                                        .builder()
                                        .textInferenceConfig(TextInferenceConfig.builder()
                                                .maxTokens(4096)
                                                .temperature(0.5F)
                                                .topP(0.5F)
                                                .build())
                                        .build())
                                .promptTemplate(PromptTemplate.builder().textPromptTemplate(GENFULLTRIAGE).build())
                                .build())
                        .build();
        RetrieveAndGenerateConfiguration config = RetrieveAndGenerateConfiguration.builder()
                .knowledgeBaseConfiguration(knowledgeBaseConfig)
                .type("KNOWLEDGE_BASE")
                .build();
        RetrieveAndGenerateInput retrieveAndGenerateInput = RetrieveAndGenerateInput.builder()
                .text(userMessage.getMessage())
                .build();
        RetrieveAndGenerateRequest request = RetrieveAndGenerateRequest.builder()
                .input(retrieveAndGenerateInput)
                .retrieveAndGenerateConfiguration(config)
                .build();
        return request;
    }


    private static final String ORCHFULLTRIAGE = """
        You are a triage query creation agent.
    
        You will be provided with structured patient data extracted from a symptom description, including fields like age, symptoms, vital signs (heart rate, blood pressure, etc.), pain score, and clinical risk indicators.
        You will also receive predictions from two machine learning models:
        - A Bayesian Network model that outputs probabilities for triage levels (low, medium, urgent)
        - A Logistic Regression model that also outputs triage probabilities
    
        Your task is to generate a concise query  that will be used to retrieve clinically similar cases from a triage case database.
    
        Use only the most clinically significant features such as:
        - Presenting symptoms 
        - Abnormal vital signs 
        - High-risk conditions 
        - Patient age 
    
        Do NOT include:
        - Model probabilities
        - Encounter type
        - Exact numeric values that are not medically significant
    
        Here is the full structured patient summary and model predictions:
        $conversation_history$
        
        $output_format_instructions$
    """;

    private static final String GENFULLTRIAGE = """
    You are a clinical triage decision support assistant.

    You will be given:
    - A structured summary of the patient's condition (symptoms, vital signs, risk indicators)
    - Triage predictions from two models:
      • Bayesian Network — outputs probability distribution over triage levels
      • Logistic Regression — outputs probability distribution over triage levels
    - A list of similar past triage cases retrieved from a medical case database

    Your task is to determine the most appropriate **triage category**, balancing all three input signals:
    - Patient’s structured data
    - Model predictions
    - Retrieved cases

    Triage Levels:
    - **P1** = Life-threatening, needs immediate care
    - **P2** = Urgent but stable
    - **P3** = Minor/delayed care
    - **Not Breathing** = Deceased/CPR required

    ---
    Input Data:
    $conversation_history$

    Retrieved Cases:
    $search_results$

    Respond using **strictly valid JSON** with the following structure:

    {
      "triage_level": "Low" | "Medium" | "High" ,
      "justification": "Explain the decision using symptoms, vitals, model predictions, and matched cases.",
      "supporting_evidence": "summarize the most relevant retrieved cases"
    }
""";




    private static final String PROMPTCKSTRIAGE = """
            
            You are a clinical triage decision support assistant.
            
            You will be given:
            - A short clinical summary of a patient’s symptoms and condition.
            - Several similar triage cases retrieved from a database of past emergency incidents.
            
            Your task is to determine the correct **triage category** (P1, P2, P3, or Deceased) using the **Ten Second Triage (TST)** algorithm used by NHS. Base your decision strictly on the TST decision flow:
            - Can the patient walk?
            - Are they breathing?
            - Are they talking?
            - Do they have severe bleeding?
            - Do they have a penetrating injury?
            
            ---
            
            Please follow this format:
            
            Triage Level: [P1 / P2 / P3 / Not Breathing] \s
            Justification: [Explain using symptoms and match to TST logic] \s
            Supporting Evidence: [Copy the source chunk Verbatim from the search result]
            
            ---
            Patient Condition:
            $conversation_history$
            
            Retrieved Clinical Evidence:
            $search_results$
            

            """;


    public Response llmJsonParser(TriageInput input) {
        try {
            // Validate input
            if (input == null || input.getMessage() == null || input.getMessage().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Input message cannot be empty").build();
            }
            BedrockRuntimeAsyncClient client = getAsyncClient2();
            String requestBody = "";
            String prompt = LLMPARSER.replace("{{symptomText}}", requestClearer(input.getMessage()));
            requestBody = "{"
                    + "\"prompt\": \"" + requestClearer(prompt) +"\\n"
                    + END_MODEL_CARD + "\","
                    + "\"temperature\": 0.7,"
                    + "\"top_p\": 0.8,"
                    + "\"max_gen_len\": 2048"
                    + "}";


            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId("meta.llama3-70b-instruct-v1:0")
                    .contentType("application/json")
                    .accept("application/json")
                    .body(SdkBytes.fromUtf8String(requestBody))
                    .build();

            CompletableFuture<InvokeModelResponse> responseFuture = client.invokeModel(request);
            InvokeModelResponse modelResponse = responseFuture.get();
            String modelOutput = modelResponse.body().asUtf8String();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
            BedrockModelResponse bedrockResponse = objectMapper.readValue(modelOutput, BedrockModelResponse.class);
            bedrockResponse.setGeneration(bedrockResponse.getGeneration().substring(bedrockResponse.getGeneration().indexOf("{"),bedrockResponse.getGeneration().lastIndexOf("}")+1));
            ParsedTriageData parsedTriageData = objectMapper.readValue(bedrockResponse.getGeneration(), ParsedTriageData.class);
            return Response.status(Response.Status.OK).entity(parsedTriageData).build();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("Failed to parse triage input: " + e.getMessage())
                    .build();
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Response llmJsonParserBayes(TriageInput input) {
        try {
            // Validate input
            if (input == null || input.getMessage() == null || input.getMessage().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Input message cannot be empty").build();
            }
            BedrockRuntimeAsyncClient client = getAsyncClient2();
            String requestBody = "";
            String prompt = LLMPARSERBAYES.replace("{{symptomText}}", requestClearer(input.getMessage()));
            requestBody = "{"
                    + "\"prompt\": \"" + requestClearer(prompt) +"\\n"
                    + END_MODEL_CARD + "\","
                    + "\"temperature\": 0.7,"
                    + "\"top_p\": 0.8,"
                    + "\"max_gen_len\": 2048"
                    + "}";


            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId("meta.llama3-70b-instruct-v1:0")
                    .contentType("application/json")
                    .accept("application/json")
                    .body(SdkBytes.fromUtf8String(requestBody))
                    .build();

            CompletableFuture<InvokeModelResponse> responseFuture = client.invokeModel(request);
            InvokeModelResponse modelResponse = responseFuture.get();
            String modelOutput = modelResponse.body().asUtf8String();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
            BedrockModelResponse bedrockResponse = objectMapper.readValue(modelOutput, BedrockModelResponse.class);
            bedrockResponse.setGeneration(bedrockResponse.getGeneration().substring(bedrockResponse.getGeneration().indexOf("{"),bedrockResponse.getGeneration().lastIndexOf("}")+1));
            BayesianRequest parsedTriageData = objectMapper.readValue(bedrockResponse.getGeneration(), BayesianRequest.class);
            return Response.status(Response.Status.OK).entity(parsedTriageData).build();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("Failed to parse triage input: " + e.getMessage())
                    .build();
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }



    public static final String LLMPARSER = """
        You are a clinical information extractor.

        Your task is to analyze patient symptom descriptions and convert them into structured JSON fields to support triage.

        Use only the information provided in the input text. If something is not mentioned, leave the field as null.

        Return only valid JSON.

        --- INPUT TEXT ---
        {{symptomText}}
        --- END INPUT TEXT ---

        Return this structure:

        {
          "heart_rate": <int or null>,
          "respiratory_rate": <int or null>,
          "systolic_bp": <int or null>,
          "diastolic_bp": <int or null>,
          "pain_score": <int (0–10) or null>,
          "symptoms": [list of lowercased symptom phrases],
          "risk_conditions": [list of matched risk keywords like 'stroke', 'sepsis', etc.],
          "age": <int or null>,
          "gender": <string or null>,
          "encounter_context": <string — 'emergency', 'outpatient', 'inpatient', 'wellness', 'urgentcare'>
        }
        """;


    public static final String LLMPARSERBAYES = """
            public static final String LLMPARSERBAYES = ""\"
            You are a clinical information extractor.
            Your task is to analyze a natural language description of a patient case and convert it into a **fully structured JSON object** for Bayesian triage prediction.
            You must return **all expected fields**, even if they are not mentioned in the input. \s
            If a value is not explicitly stated in the input, set it to `null`.
            
            
            ---
            
            ### INPUT TEXT:
            {{symptomText}}
            
            ---
            
            ### Output Rules:
            - **Only extract what is present** in the input.
            - Do **not infer or guess** missing information.
            - Use the exact property names listed below.
            - All fields must be included in the output (even if null).
            - All variables must be integers or null.
            - For vital signs like heart rate, blood pressure, pain score, age, etc., use:
                  0 = low, 1 = normal/moderate, 2 = high.
            - For binary risk conditions (e.g., "has_diabetes"), use:
                  1 if present, 0 if explicitly ruled out, null if not mentioned.
            - Do not guess. If the input doesn't mention the condition or value, set it to null.
            ---
            
            ### Expected JSON Output Format:
            
            ```json
            {
              "has_hypertension": null,
              "condition_context": null,
              "has_stroke": null,
              "has_sepsis_disorder": null,
              "has_cardiac_arrest": null,
              "has_history_of_cardiac_arrest_situation": null,
              "Systolic Blood Pressure": null,
              "age": null,
              "has_history_of_myocardial_infarction_situation": null,
              "has_myocardial_infarction": null,
              "has_stress_finding": null,
              "Body Mass Index": null,
              "Body Weight": null,
              "has_coronary_heart_disease": null,
              "has_respiratory_distress_finding": null,
              "has_pneumonia_disorder": null,
              "has_hypoxemia_disorder": null,
              "Body Height": null,
              "has_childhood_asthma": null,
              "has_reports_of_violence_in_the_environment_finding": null,
              "has_diabetic_renal_disease_disorder": null,
              "has_pneumonia": null,
              "has_prediabetes": null,
              "has_chronic_kidney_disease_stage_1_disorder": null,
              "has_septic_shock_disorder": null,
              "has_acute_respiratory_failure_disorder": null,
              "has_alzheimer's_disease_disorder": null,
              "has_suspected_covid-19": null,
              "has_anemia_disorder": null,
              "has_hypertriglyceridemia_disorder": null,
              "has_diabetes": null,
              "has_metabolic_syndrome_x_disorder": null,
              "has_covid-19": null,
              "Respiratory rate": null,
              "has_osteoporosis_disorder": null,
              "has_sepsis_caused_by_virus_disorder": null,
              "has_acute_pulmonary_embolism_disorder": null,
              "has_pathological_fracture_due_to_osteoporosis_disorder": null,
              "has_otitis_media": null,
              "has_fever_finding": null,
              "Diastolic Blood Pressure": null,
              "ENCOUNTERCLASS_urgentcare": null,
              "has_neoplasm_of_prostate": null,
              "Heart rate": null,
              "has_acute_respiratory_distress_syndrome_disorder": null,
              "has_carcinoma_in_situ_of_prostate_disorder": null,
              "has_cough_finding": null,
              "has_neuropathy_due_to_type_2_diabetes_mellitus_disorder": null,
              "has_sore_throat_symptom_finding": null,
              "has_hyperglycemia_disorder": null,
              "triage_level": null
            }          
        """;

    public static final String Prompt = """
        You are a clinical information extractor.
        
        Your task is to analyze patient symptom descriptions and convert them into structured JSON fields to support triage.
        
        Use only the information provided in the input text. If something is not mentioned, leave the field as null.
        
        Return only valid JSON.
        
        --- INPUT TEXT ---
        {{symptomText}}
        --- END INPUT TEXT ---
        
        Return this structure:  
        {
          "id": 6,
          "age": 34,
          "body_height_cm": 183.1,
          "bmi": 24.4,
          "body_weight_kg": 81.8,
          "body_temperature_c": 37.86562009419152,
          "diastolic_bp": 85.0,
          "heart_rate": 80.0,
          "pain_severity_score": 4.0,
          "respiratory_rate": 13.0,
          "systolic_bp": 135.0,
          "conditions": [
            "acute bronchitis (disorder)",
            "full-time employment (finding)",
            "limited social contact (finding)",
            "part-time employment (finding)",
            "received certificate of high school equivalency (finding)",
            "risk activity involvement (finding)",
            "social isolation (finding)",
            "sprain of ankle",
            "stress (finding)",
            "victim of intimate partner abuse (finding)",
            "viral sinusitis (disorder)"
          ],
          "triage_level": 0,
          "triage_reason": "Vitals and symptoms within normal limits",
          "gender_m": 1,
          "encounterclass_emergency": 0,
          "encounterclass_inpatient": 0,
          "encounterclass_outpatient": 0,
          "encounterclass_urgentcare": 0,
          "encounterclass_wellness": 1
        }
        """;

    private static final List<String> knownConditions = loadConditions();

    public static List<String> loadConditions() {
        try (InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("known_conditions.txt")) {

            if (in == null) {
                throw new FileNotFoundException("known_conditions.txt not found in resources");
            }

            return new BufferedReader(new InputStreamReader(in))
                    .lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load known conditions", e);
        }
    }

    public double[] buildFullFeatureVector(ParsedTriageData parsed) {
        double[] base = parsed.toFeatureVector(); // returns [age, hr, bp, etc.]
        double[] full = new double[base.length + knownConditions.size()];
        System.arraycopy(base, 0, full, 0, base.length);

        Set<String> patientKeywords = new HashSet<>();
        if (parsed.getSymptoms() != null) patientKeywords.addAll(parsed.getSymptoms());
        if (parsed.getRisk_conditions() != null) patientKeywords.addAll(parsed.getRisk_conditions());

        for (int i = 0; i < knownConditions.size(); i++) {
            String feature = knownConditions.get(i);

            // Smart check for both condition matches and exact features
            boolean matched = false;

            if (feature.startsWith("has_")) {
                String keyword = feature.replace("has_", "").replace("_", " ");
                matched = patientKeywords.contains(keyword);
            } else if (feature.startsWith("GENDER_")) {
                matched = parsed.getGender() != null && feature.equals("GENDER_" + parsed.getGender().toUpperCase());
            } else if (feature.startsWith("ENCOUNTERCLASS_")) {
                matched = parsed.getEncounter_context() != null &&
                        feature.equals("ENCOUNTERCLASS_" + parsed.getEncounter_context().toLowerCase());
            }

            full[base.length + i] = matched ? 1.0 : 0.0;
        }

        return full;
    }


    private static final Map<String, String> nameMapping = createNameMapping();

    private static Map<String, String> createNameMapping() {
        Map<String, String> map = new HashMap<>();
        map.put("systolic_bp", "Systolic Blood Pressure");
        map.put("diastolic_bp", "Diastolic Blood Pressure");
        map.put("heart_rate", "Heart rate");
        map.put("pain_score", "Pain severity - 0-10 verbal numeric rating [Score] - Reported");
        map.put("respiratory_rate", "Respiratory rate");
        map.put("body_temperature", "Body temperature");
        map.put("age", "age");
        map.put("condition_context", "condition_context");
        map.put("has_myocardial_infarction", "has_myocardial_infarction");
        map.put("has_diabetes", "has_diabetes");
        map.put("has_cardiac_arrest", "has_cardiac_arrest");
        map.put("has_pneumonia_disorder", "has_pneumonia_disorder");
        return map;
    }

    public Map<String, String> buildBayesEvidenceMap(ParsedBayes parsed) {
        System.out.println("Parsed Bayes data: " + parsed);
        Map<String, String> rawEvidence = new HashMap<>();

        // --- Discretize vitals ---
        if (parsed.getHeartRate() != null) {
            int level = (parsed.getHeartRate() < 60) ? 0 : (parsed.getHeartRate() <= 100 ? 1 : 2);
            rawEvidence.put("Heart rate", String.valueOf(level));
        }

        if (parsed.getSystolicBp() != null) {
            int level = (parsed.getSystolicBp() < 90) ? 0 : (parsed.getSystolicBp() <= 140 ? 1 : 2);
            rawEvidence.put("Systolic Blood Pressure", String.valueOf(level));
        }

        if (parsed.getDiastolicBp() != null) {
            int level = (parsed.getDiastolicBp() < 60) ? 0 : (parsed.getDiastolicBp() <= 90 ? 1 : 2);
            rawEvidence.put("Diastolic Blood Pressure", String.valueOf(level));
        }

        if (parsed.getRespiratoryRate() != null) {
            int level = (parsed.getRespiratoryRate() < 12) ? 0 : (parsed.getRespiratoryRate() <= 20 ? 1 : 2);
            rawEvidence.put("Respiratory rate", String.valueOf(level));
        }

        if (parsed.getPainScore() != null) {
            int level = (parsed.getPainScore() <= 3) ? 0 : (parsed.getPainScore() <= 6 ? 1 : 2);
            rawEvidence.put("Pain severity - 0-10 verbal numeric rating [Score] - Reported", String.valueOf(level));
        }

        if (parsed.getAge() != null) {
            int level = (parsed.getAge() < 30) ? 0 : (parsed.getAge() <= 60 ? 1 : 2);
            rawEvidence.put("age", String.valueOf(level));
        }

        // --- Encode condition_context ---
        Map<String, Integer> conditionContextMap = Map.of(
                "cardiac_related", 0,
                "diabetes_related", 1,
                "mental_health", 2,
                "mixed", 3,
                "none", 4,
                "unknown", 5
        );
        if (parsed.getConditionContext() != null) {
            Integer mapped = conditionContextMap.get(parsed.getConditionContext().toLowerCase());
            if (mapped != null) {
                rawEvidence.put("condition_context", String.valueOf(mapped));
            } else {
                System.out.println("⚠Unknown condition_context: " + parsed.getConditionContext());
            }
        }

        // --- Binary risk conditions ---
        if (parsed.getRiskConditions() != null) {
            for (String risk : parsed.getRiskConditions()) {
                String key = "has_" + risk.toLowerCase().replace(" ", "_");
                rawEvidence.put(key, "1");
            }
        }

        System.out.println("Final evidence passed to inference engine: " + rawEvidence);
        return rawEvidence;
    }


    public Response predictWithBayesian(BayesianRequest bayesianRequest){
            return bayesianRestClient.BayesianPrediction(bayesianRequest);
    }


    public Response forwardFeedToRag(FinalTriageRequest finalTriageRequest, TriageInput triageInput) throws Exception{
        BedrockAgentRuntimeAsyncClient asyncClient3 = getAsyncClient3();
        TriageInput ragInput = new TriageInput();
        String combinedMessage = """
                Patient Description:
                %s
                
                Model Outputs:
                Bayesian Triage Prediction → Final: %s
                  - Low: %s
                  - Medium: %s
                  - Urgent: %s
                
                Logistic Regression Triage Prediction → Final: %s
                  - Low: %s
                  - Medium: %s
                  - Urgent: %s
                """.formatted(
                                triageInput.getMessage(),
                                finalTriageRequest.getBayesianNetResult().getFinalBayesianNetTriage(),
                                finalTriageRequest.getBayesianNetResult().getLowBNTriageProbabllity(),
                                finalTriageRequest.getBayesianNetResult().getMediumBNTriageProbabllity(),
                                finalTriageRequest.getBayesianNetResult().getUrgentBNTriageProbabllity(),
                                finalTriageRequest.getLogisticRegressionResult().getFinalLogisticTriage(),
                                finalTriageRequest.getLogisticRegressionResult().getLowLRTriageProbabllity(),
                                finalTriageRequest.getLogisticRegressionResult().getMediumLRTriageProbabllity(),
                                finalTriageRequest.getLogisticRegressionResult().getUrgentLRTriageProbabllity()
                        );

        ragInput.setMessage(combinedMessage);
        RetrieveAndGenerateRequest retrieveAndGenerateRequest =  ragTriageTestRequest(ragInput);
        System.out.println("Rag Request created" + retrieveAndGenerateRequest);
        System.out.println("Calling LLM for final Triage decision");

        CompletableFuture<RetrieveAndGenerateResponse> retrieveAndGenerateResponse = asyncClient3.retrieveAndGenerate(retrieveAndGenerateRequest);
        ResponseRag responseRag = new ResponseRag();
        ObjectMapper  objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        responseRag.setGeneration(retrieveAndGenerateResponse.get().output().toString());
        LLMTriageOutput llmTriageOutput = objectMapper.readValue(responseRag.getGeneration().substring(responseRag.getGeneration().indexOf("{"),responseRag.getGeneration().lastIndexOf("}")+1), LLMTriageOutput.class);

        Map<String, String> responseMapllm = new LinkedHashMap<>();
        responseMapllm.put("triage_level", llmTriageOutput.getTriage_level());
        responseMapllm.put("justification", llmTriageOutput.getJustification());
        responseMapllm.put("supporting_evidence", llmTriageOutput.getSupporting_evidence());
        Object genericLlmObject = objectMapper.convertValue(responseMapllm, Object.class);

        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("logisticRegressionResult", finalTriageRequest.getLogisticRegressionResult());
        responseMap.put("bayesianNetResult", finalTriageRequest.getBayesianNetResult());
        responseMap.put("llmTriageResult", genericLlmObject);

        String json = objectMapper.writeValueAsString(responseMap);


        return Response.ok(responseMap).build();


    }




}







