import pandas as pd

# === Step 1: Load CSVs ===
patients = pd.read_csv("patients.csv")
conditions = pd.read_csv("conditions.csv")
observations = pd.read_csv("observations.csv")

# === Step 2: Prepare Patients (age + gender) ===
patients['age'] = pd.to_datetime("2025-01-01") - pd.to_datetime(patients["BIRTHDATE"])
patients['age'] = patients['age'].dt.days // 365
patients = patients[["Id", "age", "GENDER"]].rename(columns={"Id": "PATIENT"})

# === Step 3: Collect All Conditions Per Patient ===
cond_grouped = conditions.groupby("PATIENT")["DESCRIPTION"].apply(lambda x: ", ".join(set(x))).reset_index()

# === Step 4: Extract Latest Vitals for Each Patient ===
def latest_observation(df, label):
    return (
        df[df["DESCRIPTION"] == label]
        .sort_values("DATE")
        .groupby("PATIENT")
        .tail(1)[["PATIENT", "VALUE"]]
        .rename(columns={"VALUE": label})
    )

temp = latest_observation(observations, "Body Temperature")
bp_sys = latest_observation(observations, "Systolic Blood Pressure")
bp_dia = latest_observation(observations, "Diastolic Blood Pressure")

# === Step 5: Merge Everything ===
df = patients.merge(cond_grouped, on="PATIENT", how="left")\
             .merge(temp, on="PATIENT", how="left")\
             .merge(bp_sys, on="PATIENT", how="left")\
             .merge(bp_dia, on="PATIENT", how="left")

df = df.rename(columns={"DESCRIPTION": "conditions"})

# === Step 6: Assign Triage Levels ===
def assign_triage(row):
    try:
        temp = float(row["Body Temperature"]) if pd.notna(row["Body Temperature"]) else 98.6
        bp = float(row["Systolic Blood Pressure"]) if pd.notna(row["Systolic Blood Pressure"]) else 120
        cond = str(row["conditions"]).lower()

        if "myocardial infarction" in cond or "stroke" in cond or bp > 160:
            return "urgent"
        elif temp > 100.4 or "flu" in cond or "pneumonia" in cond or bp > 135:
            return "moderate"
        elif temp < 100 and bp < 135 and not any(word in cond for word in ["infarction", "pneumonia", "stroke", "sepsis"]):
            return "low"
        else:
            return "moderate"
    except:
        return "low"

df["triage_level"] = df.apply(assign_triage, axis=1)

# === Step 7: Save or View ===
df.to_csv("final_triage_dataset.csv", index=False)
print(df["triage_level"].value_counts())
