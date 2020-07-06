#!/usr/bin/env python
import sys
import pandas as pd
from datetime import datetime
import json

batchName = 'a190'
startDate = '05.01.2020'
startDateStr = '04.05.2020'
endDate = '06.06.2020'
endDateStr = '04.06.2020'

chartTitle = '%s (%s-%s)' % ('Batch a190', startDateStr, endDateStr)

batchClass_N = 0
batchClass_L1 = 25000
batchClass_L2 = 250000
batchClass_L3 = 1000000

classN = "Täglich"

if(len(sys.argv) <= 1):
    print("No Excel File given!", flush=True)
    sys.exit(1)
excel_data = sys.argv[1]

df = pd.read_excel(excel_data, header=0, usecols="A:V", skiprows=0, nrows=None)


data = df[(df["Batch"] == batchName) & (df["Zeitpunkt Start"] >= startDate) & (df["Zeitpunkt Start"] <= endDate)]
data = data.sort_values(by=['Batch', 'Zeitpunkt Start'])

data = data[[
'Batch',
'Return Code',
'Zeitpunkt Start',
'Zeitpunkt Ende',
'PRO: Verarbeitete Elemente',
'PRO: nicht verarbeitete Elemente'
]]


x_data = data["Zeitpunkt Start"].dt.strftime("%d-%m-%Y").to_numpy()

x_data_class_N = data[(data['PRO: Verarbeitete Elemente'] > batchClass_N) & (data['PRO: Verarbeitete Elemente'] < batchClass_L1)]['Zeitpunkt Start'].dt.date
x_data_class_L1 = data[(data['PRO: Verarbeitete Elemente'] > batchClass_L1) & (data['PRO: Verarbeitete Elemente'] < batchClass_L2)]['Zeitpunkt Start'].dt.date
x_data_class_L2 = data[(data['PRO: Verarbeitete Elemente'] > batchClass_L2) & (data['PRO: Verarbeitete Elemente'] < batchClass_L3)]['Zeitpunkt Start'].dt.date
x_data_class_L3 = data[(data['PRO: Verarbeitete Elemente'] > batchClass_L3)]['Zeitpunkt Start'].dt.date


y_data_verarbeitet = data['PRO: Verarbeitete Elemente']

y_data_verarbeitet_class_N = data[(data['PRO: Verarbeitete Elemente'] > batchClass_N) & (data['PRO: Verarbeitete Elemente'] < batchClass_L1)]
y_data_verarbeitet_class_L1 = data[(data['PRO: Verarbeitete Elemente'] > batchClass_L1) & (data['PRO: Verarbeitete Elemente'] < batchClass_L2)]
y_data_verarbeitet_class_L2 = data[(data['PRO: Verarbeitete Elemente'] > batchClass_L2) & (data['PRO: Verarbeitete Elemente'] < batchClass_L3)]
y_data_verarbeitet_class_L3 = data[(data['PRO: Verarbeitete Elemente'] > batchClass_L3)]

y_data_nicht_verarbeitet = data['PRO: nicht verarbeitete Elemente']


# ---------------------------------------------------------------------------------------------
# Grafik 1: Basierend auf Grafik 3 mit 2 Traces und Button zum Umschalten zwischen lin. und log.
# Darstellung.
# ---------------------------------------------------------------------------------------------


def appendTrace(traces, x, y, name, visible):
    traces.append({
        "x": x.tolist(), # convert np.ndarray to python list
        "y": y.tolist(),
        "type": "bar",
        "name": name,
        "visible": visible,
        "hoverinfo": "x+y+text"
        })

traces = []
appendTrace(traces, x=x_data, y=y_data_verarbeitet.to_numpy(), name='Verarbeitete Elemente (Gesamt)', visible=True )
appendTrace(traces, x=x_data, y=y_data_nicht_verarbeitet.to_numpy(), name='nicht verarbeitete Elemente', visible=True)

updatemenus = list([
    dict(active=0,
		 type = "buttons",
		 direction = "down",
         buttons=list([
            dict(label='log.',
                 method='update',
                 args=[{'visible': [True, True]},
                       {'title': '%s - %s' % (chartTitle, 'Gesamt (logarithmisch)'),
                        'yaxis': {'type': 'log'}}]),
            dict(label='linear',
                 method='update',
                 args=[{'visible': [True, True]},
                       {'title': '%s - %s' % (chartTitle, 'Gesamt (linear)'),
                        'yaxis': {'type': 'linear'}}])
            ]),
        )
    ])
title = '%s - %s' % (chartTitle, 'Gesamt')
layout = {
        "updatemenus": updatemenus,
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
          "type": "log"
        },
        "autosize": True,
        "margin": {
          "autoexpand": True,
          "l": 100,
          "r": 20,
          "t": 110,
        },
        "showlegend": True,
        "title": title
      }
      
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
