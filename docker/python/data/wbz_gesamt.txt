#!/usr/bin/env python

import sys
import os
import json
import pandas as pd
import numpy as np
import math
from datetime import datetime

if(len(sys.argv) <= 1):
    print("No Excel File given!", flush=True)
    sys.exit(1)
excel_data = sys.argv[1]

with pd.ExcelFile(excel_data) as xls:
    df1 = pd.read_excel(xls, 0, header=[0])
    df2 = pd.read_excel(xls, 1)

df1_count = df1.groupby('Entscheidungsart').count()
numNichtBewilligt = df2.count()[['BG-Nummer']].iloc[0]
numVorlaeufig = df1_count.iloc[1][0]

numEndgueltig = df1_count.iloc[0][0]

traces = []

traces.append({
    "type": "pie",
    "values": [int(numNichtBewilligt), int(numVorlaeufig), int(numEndgueltig)],
    "labels": ["Nicht witerbewilligt", "Vorläufig bewilligt", "Endgültig bewilligt"],
    "textinfo": "label+percent",
    "textposition": "outside",
    "automargin": True
})

title = 'WBZ Gesamt A542'
dateTimeObj = datetime.now()
timestampStr = dateTimeObj.strftime("%d.%m.%Y (%H:%M)")

result = {
    "title": title,
    "traces": traces,
    "updateTime" : timestampStr
}


print(json.dumps(result))
sys.stdout.flush()
