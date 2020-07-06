import sys
import os
import json
import pandas as pd
import numpy as np
import math

if(len(sys.argv) <= 1):
    print("No Excel File given!", flush=True)
    sys.exit(1)
excel_data = sys.argv[1]

with pd.ExcelFile(excel_data) as xls:
    df1 = pd.read_excel(xls, 0, header=[0])
    df2 = pd.read_excel(xls, 1)

df2.replace('^Zu der Person \w{10} existiert', 'Zu der Person <ID> existiert', regex=True, inplace=True)
df2.replace('^Im Monat, in dem die Person \w{10} das','Im Monat, in dem die Person <ID> das', regex=True, inplace=True)
df2.replace('^Für die Person \w{10}', 'Für die Person <ID>', regex=True, inplace=True)
df2.replace('in denen die Person \w{10} an', 'in denen die Person <ID> an', regex=True, inplace=True)
df2.replace('ausgewählte Person \w{10} ', 'ausgewählte Person <ID>', regex=True, inplace=True)
df2.replace('erwerbsfähigen Person \w{10} ', 'erwerbsfähigen Person <ID> ', regex=True, inplace=True)
df2.replace('Erwerbsfähigkeit für die Person \w{10}.', 'Erwerbsfähigkeit für die Person <ID>.', regex=True, inplace=True)
df2.replace('Kundennummer \w{10} gefundenen', 'Kundennummer <ID> gefundenen', regex=True, inplace=True)
df2.replace('Fallzeitraum \d{2}.\d{2}.2020-\d{2}.\d{2}.2020 ', 'Fallzeitraum <xx>.<yy>.2020-<xx>.<yy>.2020 ', regex=True, inplace=True)
df2.replace('Fallzeitraum \d{2}.\d{2}.2019-\d{2}.\d{2}.2020 ', 'Fallzeitraum <xx>.<yy>.2019-<xx>.<yy>.2020 ', regex=True, inplace=True)
df2.replace('Kundennummer \w{10} in STEP ', 'Kundennummer <ID> in STEP ', regex=True, inplace=True)
df2.replace('Für die Zeit ab \d{2}.\d{2}.2020 ', 'Für die Zeit ab <xx>.<yy>.2020 ', regex=True, inplace=True)
df2.replace('Für die Zeit ab \d{2}.\d{2}.2021 ', 'Für die Zeit ab <xx>.<yy>.2021 ', regex=True, inplace=True)

df2_counted = df2.groupby('Protokollgrund').count().apply(np.array).reset_index()

traces = []
values = []
labels = []

for i in range(len(df2_counted)): 
  labels.append(df2_counted.loc[i, "Protokollgrund"])
  values.append(int(df2_counted.loc[i, "BG-Nummer"]))

traces.append({
    "type": "pie",
    "values": values,
    "labels": labels,
    "textinfo": "label+percent",
    "textposition": "outside",
    "automargin": True,
    "hoverinfo": 'label+percent+name',
})

result = {
    "title": 'WBZ A542 - Nicht weiterbewilligt Gründe',
    "traces": traces,
    "layout": {
        "showlegend": False,
        "autosize": True,
        "margin": {
          "autoexpand": True,
          "l": 100,
          "r": 20,
          "t": 110,
        },
    }
}

print(json.dumps(result))
sys.stdout.flush()
