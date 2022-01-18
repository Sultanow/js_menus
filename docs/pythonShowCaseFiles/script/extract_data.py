#!/usr/bin/env python
import json
import math
import os
import sys
import re

import numpy as np
import pandas as pd
from datetime import datetime

if(len(sys.argv) <= 1):
    print("No Excel File given!", flush=True)
    sys.exit(1)
excel_data = sys.argv[1]

df = pd.read_excel(excel_data, header=1, usecols="A:I", skiprows=0, nrows=19)

labels = df.columns.astype(str).to_list()
labels = labels[1:]
shortLables = []
for label in labels:
  shortLables.append(re.sub(" 00:00:00", "", label))
num_series = len(shortLables)
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
        "name": shortLables[n],
        "line":dict(width=2),
        "connectgaps":True,
        "hoverinfo": "x+y+text"
    })
title = 'Anwenderzahlen'

layout = {
        "xaxis": {
          "showline": True,
          "showgrid": True,
          "showticklabels": True,
          "linewidth": 2,
          "ticks": "outside",
          "tickfont": {
            "family": "Arial",
            "size": 12,
          }
        },
        "yaxis": {
          "showgrid": True,
          "zeroline": True,
          "showline": True,
          "showticklabels": True,
        },
        "autosize": True,
        "margin": {
          "autoexpand": True,
          "l": 100,
          "r": 20,
          "t": 110,
        },
        "showlegend": True,
        "title": title,
        "separators":".,"
      }
# Converting datetime object to string
dateTimeObj = datetime.now()
timestampStr = dateTimeObj.strftime("%d.%m.%Y (%H:%M)")

result = {
    "title": title,
    "traces": traces,   
    "layout": layout,
    "updateTime" : timestampStr
}

print(json.dumps(result))
sys.stdout.flush()
