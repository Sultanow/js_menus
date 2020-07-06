#!/usr/bin/env python
import sys
import pandas as pd
import plotly.express as px
import plotly.graph_objs as go

batchName = 'a190'
startDate = '01.01.2020'
endDate = '31.01.2020'

chartTitle = '%s (%s-%s)' % ('Batch a190', startDate, endDate)

batchClass_N = 0
batchClass_L1 = 25000
batchClass_L2 = 250000
batchClass_L3 = 1000000

if(len(sys.argv) <= 1):
    print("No Excel File given!", flush=True)
    sys.exit(1)
excel_data = sys.argv[1]

df = pd.read_excel(excel_data, header=0, usecols="A:V", skiprows=0, nrows=None)

print(df.info())

data = df[(df["Batch"] == batchName) & (df["Zeitpunkt Start"] > startDate) & (df["Zeitpunkt Start"] < endDate)]
data = data.sort_values(by=['Batch', 'Zeitpunkt Start'])

data = data[[
'Batch',
'Return Code',
'Zeitpunkt Start',
'Zeitpunkt Ende',
'PRO: Verarbeitete Elemente',
'PRO: nicht verarbeitete Elemente'
]]

print(data.to_string())

x_data = data['Zeitpunkt Start'].dt.date

x_data_class_N = data[(data['PRO: Verarbeitete Elemente'] > batchClass_N) & (data['PRO: Verarbeitete Elemente'] < batchClass_L1)]['Zeitpunkt Start'].dt.date
x_data_class_L1 = data[(data['PRO: Verarbeitete Elemente'] > batchClass_L1) & (data['PRO: Verarbeitete Elemente'] < batchClass_L2)]['Zeitpunkt Start'].dt.date
x_data_class_L2 = data[(data['PRO: Verarbeitete Elemente'] > batchClass_L2) & (data['PRO: Verarbeitete Elemente'] < batchClass_L3)]['Zeitpunkt Start'].dt.date
x_data_class_L3 = data[(data['PRO: Verarbeitete Elemente'] > batchClass_L3)]['Zeitpunkt Start'].dt.date

print(x_data.to_string())
print('N\n' + x_data_class_N.to_string())
print('L1\n' + x_data_class_L1.to_string())
print('L2\n' + x_data_class_L2.to_string())
print('L3\n' + x_data_class_L3.to_string())

y_data_verarbeitet = data['PRO: Verarbeitete Elemente']

y_data_verarbeitet_class_N = data[(data['PRO: Verarbeitete Elemente'] > batchClass_N) & (data['PRO: Verarbeitete Elemente'] < batchClass_L1)]
y_data_verarbeitet_class_L1 = data[(data['PRO: Verarbeitete Elemente'] > batchClass_L1) & (data['PRO: Verarbeitete Elemente'] < batchClass_L2)]
y_data_verarbeitet_class_L2 = data[(data['PRO: Verarbeitete Elemente'] > batchClass_L2) & (data['PRO: Verarbeitete Elemente'] < batchClass_L3)]
y_data_verarbeitet_class_L3 = data[(data['PRO: Verarbeitete Elemente'] > batchClass_L3)]

y_data_nicht_verarbeitet = data['PRO: nicht verarbeitete Elemente']

print('N\n' + y_data_verarbeitet_class_N.to_string())
print('L1\n' + y_data_verarbeitet_class_L1.to_string())
print('L2\n' + y_data_verarbeitet_class_L2.to_string())
print('L3\n' + y_data_verarbeitet_class_L3.to_string())

trace0 = go.Bar(x=x_data, y=y_data_verarbeitet, name='Verarbeitete Elemente (Gesamt)', visible=True)

#trace1 = go.Bar(x=x_data_class_N, y=y_data_verarbeitet_class_N, name='Verarbeitete Elemente (Klasse N)',   visible=True)
#trace2 = go.Bar(x=x_data_class_L1, y=y_data_verarbeitet_class_L1, name='Verarbeitete Elemente (Klasse L1)', visible=True)
#trace3 = go.Bar(x=x_data_class_L2, y=y_data_verarbeitet_class_L2, name='Verarbeitete Elemente (Klasse L2)', visible=True)
#trace4 = go.Bar(x=x_data_class_L3, y=y_data_verarbeitet_class_L3, name='Verarbeitete Elemente (Klasse L3)', visible=True)

trace1 = go.Bar(x=x_data, y=y_data_verarbeitet_class_N, name='Verarbeitete Elemente (Klasse N)',   visible=True)
trace2 = go.Bar(x=x_data, y=y_data_verarbeitet_class_L1, name='Verarbeitete Elemente (Klasse L1)', visible=True)
trace3 = go.Bar(x=x_data, y=y_data_verarbeitet_class_L2, name='Verarbeitete Elemente (Klasse L2)', visible=True)
trace4 = go.Bar(x=x_data, y=y_data_verarbeitet_class_L3, name='Verarbeitete Elemente (Klasse L3)', visible=True)

trace5 = go.Bar(x=x_data, y=y_data_nicht_verarbeitet, name='nicht verarbeitete Elemente', visible=True)

# ---------------------------------------------------------------------------------------------
# Grafik 1: Basierend auf Grafik 3 mit 2 Traces und Button zum Umschalten zwischen lin. und log.
# Darstellung.
# ---------------------------------------------------------------------------------------------

data = [trace0, trace5]

updatemenus = list([
    dict(active=0,
		 type = "buttons",
		 direction = "left",
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

layout = dict(updatemenus=updatemenus, title='%s - %s' % (chartTitle, 'Gesamt'))
fig = go.Figure(data=data, layout=layout)
fig.show()

# ---------------------------------------------------------------------------------------------
# Grafik 2: Versuch, die Darstellung zu erweitern mit eigenen Datenreihen für die 
# unterschiedlichen Batch-Klassen.
# ---------------------------------------------------------------------------------------------

data = [trace0, trace1, trace2, trace3, trace4, trace5]

updatemenus = list([
    dict(active=0,
		 type = "buttons",
		 direction = "left",
         buttons=list([
            dict(label='Gesamt',
                 method='update',
                 args=[{'visible': [True, True, True, True, True, True]},
                       {'title': '%s - %s' % (chartTitle, 'Gesamt (logarithmisch)'),
                        'yaxis': {'type': 'log'}}]),
            dict(label='Class N',
                 method='update',
                 args=[{'visible': [False, True, False, False, False, True]},
                       {'title': '%s - %s' % (chartTitle, 'Class N (linear)'),
                        'yaxis': {'type': 'linear'}}]),
            dict(label='Class L1',
                 method='update',
                 args=[{'visible': [False, False, True, False, False, True]},
                       {'title': '%s - %s' % (chartTitle, 'Class L1 (linear)'),
                        'yaxis': {'type': 'linear'}}]),
            dict(label='Class L2',
                 method='update',
                 args=[{'visible': [False, False, False, True, False, True]},
                       {'title': '%s - %s' % (chartTitle, 'Class L2 (linear)'),
                        'yaxis': {'type': 'linear'}}]),
            dict(label='Class L3',
                 method='update',
                 args=[{'visible': [False, False, False, False, True, True]},
                       {'title': '%s - %s' % (chartTitle, 'Class L3 (linear)'),
                        'yaxis': {'type': 'linear'}}])
            ]),
        )
    ])

layout = dict(updatemenus=updatemenus, title=chartTitle)
fig = go.Figure(data=data, layout=layout)
fig.show()

# ---------------------------------------------------------------------------------------------
# Beispiel aus dem Internet (siehe https://stackoverflow.com/questions/54872300/how-to-enable-and-disable-the-logarithmic-scale-as-a-viewer-in-plotly)
# ---------------------------------------------------------------------------------------------

x = [1, 2, 3]
y = [1000, 10000, 100000]
y2 = [5000, 10000, 90000]

trace1 = go.Bar(x=x, y=y, name='trace1')
trace2 = go.Bar(x=x, y=y2, name='trace2', visible=False)

data = [trace1, trace2]

updatemenus = list([
    dict(active=1,
         buttons=list([
            dict(label='Log Scale',
                 method='update',
                 args=[{'visible': [True, True]},
                       {'title': 'Log scale',
                        'yaxis': {'type': 'log'}}]),
            dict(label='Linear Scale',
                 method='update',
                 args=[{'visible': [True, False]},
                       {'title': 'Linear scale',
                        'yaxis': {'type': 'linear'}}])
            ]),
        )
    ])

layout = dict(updatemenus=updatemenus, title='Linear scale')
fig = go.Figure(data=data, layout=layout)

fig.show()