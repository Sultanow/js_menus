#!/usr/bin/env python
import json
import math
import os
import sys

import numpy as np
import pandas as pd

if(len(sys.argv) <= 1):
    print("No Excel File given!", flush=True)
    sys.exit(1)
excel_data = sys.argv[1]

df = pd.read_excel(excel_data, header=1, usecols="A:I", skiprows=0, nrows=19)

labels = df.columns.astype(str).to_list()
labels = labels[1:]
num_series = len(labels)
df_values_only = df.iloc[:,1:num_series+1]
num_values = df_values_only.shape[0]
x_scale = df.iloc[:,0].to_numpy()
x_data = [[t.strftime("%H:%M:%S") for t in r]for r in np.vstack((x_scale,)*num_series)]
y_data = []
for r in df_values_only.to_numpy().transpose().tolist():
    datapoints = []
    for v in r:
        pending = float(v)
        if math.isnan(v):
            datapoints.append(None)
        else:
            datapoints.append(v)
    y_data.append(datapoints)

traces = []
for n in range(num_series):
    traces.append({
        "x":x_data[n], 
        "y":y_data[n], 
        "mode": "lines", 
        "type": "scatter", 
        "name": labels[n],
        "line":dict(width=2),
        "connectgaps":True
    })

result = {
    "title": 'Anwenderzahlen',
    "traces": traces
}

print(json.dumps(result))
sys.stdout.flush()
