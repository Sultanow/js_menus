#!/usr/bin/env python
import json
import math
import os
import sys
import re
from datetime import datetime

traces = [
    {
        "time": "2020-05-04",
        "timetraces": [
                {
                    "x": [
                        "05:00:00",
                        "06:00:00",
                        "07:00:00",
                        "08:00:00",
                        "09:00:00",
                        "10:00:00",
                        "11:00:00",
                        "12:00:00",
                        "13:00:00",
                        "14:00:00",
                        "15:00:00",
                        "16:00:00",
                        "17:00:00",
                        "18:00:00",
                        "19:00:00",
                        "20:00:00",
                        "21:00:00",
                        "22:00:00",
                        "23:00:00"
                    ],
                    "y": [
                        1833,
                        14512,
                        24711,
                        31654,
                        33925,
                        34756,
                        34200,
                        27483,
                        28425,
                        27425,
                        20855,
                        10284,
                        4261,
                        2163,
                        1271,
                        910,
                        379,
                        107,
                        46
                    ],
                    "mode": "lines",
                    "type": "scatter",
                    "name": "2020-05-04",
                    "line": {
                        "width": 2
                    },
                    "connectgaps": True,
                    "hoverinfo": "x+y+text"
                }
        ]
    },
    {
        "time": "2020-05-05",
        "timetraces": [
                {
                    "x": [
                        "05:00:00",
                        "06:00:00",
                        "07:00:00",
                        "08:00:00",
                        "09:00:00",
                        "10:00:00",
                        "11:00:00",
                        "12:00:00",
                        "13:00:00",
                        "14:00:00",
                        "15:00:00",
                        "16:00:00",
                        "17:00:00",
                        "18:00:00",
                        "19:00:00",
                        "20:00:00",
                        "21:00:00",
                        "22:00:00",
                        "23:00:00"
                    ],
                    "y": [
                        2082,
                        15727,
                        26429,
                        32815,
                        34556,
                        34877,
                        34313,
                        26959,
                        28359,
                        27851,
                        21282,
                        10651,
                        4696,
                        2193,
                        1301,
                        882,
                        379,
                        123,
                        57
                    ],
                    "mode": "lines",
                    "type": "scatter",
                    "name": "2020-05-05",
                    "line": {
                        "width": 2
                    },
                    "connectgaps": True,
                    "hoverinfo": "x+y+text"
                }
        ]
    },
    {
        "time": "2020-05-06",
        "timetraces": [
                {
                    "x": [
                        "05:00:00",
                        "06:00:00",
                        "07:00:00",
                        "08:00:00",
                        "09:00:00",
                        "10:00:00",
                        "11:00:00",
                        "12:00:00",
                        "13:00:00",
                        "14:00:00",
                        "15:00:00",
                        "16:00:00",
                        "17:00:00",
                        "18:00:00",
                        "19:00:00",
                        "20:00:00",
                        "21:00:00",
                        "22:00:00",
                        "23:00:00"
                    ],
                    "y": [
                        2167,
                        15705,
                        25999,
                        32029,
                        33675,
                        33998,
                        33348,
                        26220,
                        27426,
                        26170,
                        18846,
                        9301,
                        3947,
                        2081,
                        1219,
                        876,
                        395,
                        136,
                        56
                    ],
                    "mode": "lines",
                    "type": "scatter",
                    "name": "2020-05-06",
                    "line": {
                        "width": 2
                    },
                    "connectgaps": True,
                    "hoverinfo": "x+y+text"
                }
        ]
    },
    {
        "time": "2020-05-07",
        "timetraces": [
                {
                    "x": [
                        "05:00:00",
                        "06:00:00",
                        "07:00:00",
                        "08:00:00",
                        "09:00:00",
                        "10:00:00",
                        "11:00:00",
                        "12:00:00",
                        "13:00:00",
                        "14:00:00",
                        "15:00:00",
                        "16:00:00",
                        "17:00:00",
                        "18:00:00",
                        "19:00:00",
                        "20:00:00",
                        "21:00:00",
                        "22:00:00",
                        "23:00:00"
                    ],
                    "y": [
                        2027,
                        14979,
                        25510,
                        31846,
                        33532,
                        33984,
                        33403,
                        26234,
                        27554,
                        26939,
                        20609,
                        11641,
                        5980,
                        2447,
                        1182,
                        825,
                        358,
                        100,
                        37
                    ],
                    "mode": "lines",
                    "type": "scatter",
                    "name": "2020-05-07",
                    "line": {
                        "width": 2
                    },
                    "connectgaps": True,
                    "hoverinfo": "x+y+text"
                }
        ]
    },
    {
        "time": "2020-05-08",
        "timetraces": [
                {
                    "x": [
                        "05:00:00",
                        "06:00:00",
                        "07:00:00",
                        "08:00:00",
                        "09:00:00",
                        "10:00:00",
                        "11:00:00",
                        "12:00:00",
                        "13:00:00",
                        "14:00:00",
                        "15:00:00",
                        "16:00:00",
                        "17:00:00",
                        "18:00:00",
                        "19:00:00",
                        "20:00:00",
                        "21:00:00",
                        "22:00:00",
                        "23:00:00"
                    ],
                    "y": [
                        1765,
                        13447,
                        22141,
                        27003,
                        27902,
                        27929,
                        27325,
                        20248,
                        10947,
                        5699,
                        3579,
                        2196,
                        1447,
                        962,
                        668,
                        496,
                        178,
                        60,
                        40
                    ],
                    "mode": "lines",
                    "type": "scatter",
                    "name": "2020-05-08",
                    "line": {
                        "width": 2
                    },
                    "connectgaps": True,
                    "hoverinfo": "x+y+text"
                }
        ]
    },
    {
        "time": "2020-05-09",
        "timetraces": [
                {
                    "x": [
                        "05:00:00",
                        "06:00:00",
                        "07:00:00",
                        "08:00:00",
                        "09:00:00",
                        "10:00:00",
                        "11:00:00",
                        "12:00:00",
                        "13:00:00",
                        "14:00:00",
                        "15:00:00",
                        "16:00:00",
                        "17:00:00",
                        "18:00:00",
                        "19:00:00",
                        "20:00:00",
                        "21:00:00",
                        "22:00:00",
                        "23:00:00"
                    ],
                    "y": [
                        25,
                        35,
                        61,
                        85,
                        88,
                        89,
                        86,
                        61,
                        54,
                        48,
                        45,
                        39,
                        34,
                        33,
                        37,
                        30,
                        25,
                        20,
                        19
                    ],
                    "mode": "lines",
                    "type": "scatter",
                    "name": "2020-05-09",
                    "line": {
                        "width": 2
                    },
                    "connectgaps": True,
                    "hoverinfo": "x+y+text"
                }
        ]
    },
    {
        "time": "2020-05-11",
        "timetraces": [
                {
                    "x": [
                        "05:00:00",
                        "06:00:00",
                        "07:00:00",
                        "08:00:00",
                        "09:00:00",
                        "10:00:00",
                        "11:00:00",
                        "12:00:00",
                        "13:00:00",
                        "14:00:00",
                        "15:00:00",
                        "16:00:00",
                        "17:00:00",
                        "18:00:00",
                        "19:00:00",
                        "20:00:00",
                        "21:00:00",
                        "22:00:00",
                        "23:00:00"
                    ],
                    "y": [
                        1990,
                        15013,
                        25367,
                        32062,
                        33988,
                        34716,
                        34250,
                        27410,
                        28536,
                        27568,
                        20871,
                        10465,
                        None,
                        None,
                        None,
                        None,
                        None,
                        None,
                        None
                    ],
                    "mode": "lines",
                    "type": "scatter",
                    "name": "2020-05-11",
                    "line": {
                        "width": 2
                    },
                    "connectgaps": True,
                    "hoverinfo": "x+y+text"
                }
        ]
    }
]

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
    "title": "TestTitle",
    "separators": ".,"
}
# Converting datetime object to string
dateTimeObj = datetime.now()
timestampStr = dateTimeObj.strftime("%d.%m.%Y (%H:%M)")

result = {
    "title": "TestTitle",
    "traces": traces,
    "layout": layout,
    "updateTime": timestampStr,
    "timeseries" : True,
    "accuracy" : "day",
    "multiple" : False,
}

print(json.dumps(result))
sys.stdout.flush()
