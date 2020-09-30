# configuration.zabbix.User
Dies ist der User für Zabbix. Der default des Dockerimages ist `Admin`

# configuration.zabbix.Password	
Dies ist das Passwort für Zabbix. Der default des Dockerimages ist `zabbix`

# configuration.zabbix.URL	
Die URL, mit der man das Zabbix erreichen kann. Im Docker bundle ist dies `http://zabbix-frontend:80/api_jsonrpc.php`, da die Kommunikation über das interne Docker Netzwerk läuft.
Bei der URL ist es wichtig die API-Schnittstelle mit anzugeben, da dies nicht automatisch an die Netzwerkadresse angehängt wird!

# configuration.zabbix.filterGroup	
Beispiel: `Hostgroup1`
# configuration.zabbix.items
Nicht verwendet aktuell.	
# configuration.frontend.title	
Der Titel ist Konfigurierbar. Beispiel:
```
New Dashboard
``` 

# configuration.frontend.logo	
Das Logo ist ein SVG Element. Beispiel:
```
<svg viewBox='5 -10 12 12' xmlns='http://www.w3.org/2000/svg' width='40px' height='20px'>
 <style>.logo 
    { font: italic 13px sans-serif; fill: white; } 
 </style> 
 <text x='0' y='2' class='logo'>
    KC
 </text>
</svg>
```
# configuration.dummy.statuswarning	
Diese Einstellung soll eine Möglichkeit bieten einen erste Vorschau auf das zukünftige Feature. Beispiel
```
[
      {
        "name" : "dev1",
        "items" : [{
          "name" : "Item1"},
          {"name" : "Item2"}
        ]
      },
      {
        "name" : "dev2",
        "items": [
          {"name": "Item3"},
          {"name": "Item4"}
        ]
      }
    ]
```

# configuration.servercompare.config
```
[
    {
        "value": {
            "Konfigurationsparameter": "Top1"
        },
        "children": [
            {
                "value": {
                    "Konfigurationsparameter": "Level2 - 1"
                },
                "children": [
                    {
                        "value": {
                            "Konfigurationsparameter": "Level3 - 1"
                        },
                        "children": [
                        ]
                    },
                    {
                        "value": {
                            "Konfigurationsparameter": "Level 3 - 2"
                        },
                        "children": [
                        ]
                    }
                ]
            },
            {
                "value": {
                    "Konfigurationsparameter": "Level2 - 2"
                },
                "children": [
                ]
            },
            {
                "value": {
                    "Konfigurationsparameter": "Level2 - 3"
                },
                "children": [
                ]
            },
            {
                "value": {
                    "Konfigurationsparameter": "Level2 - 4"
                },
                "children": [
                ]
            }
        ]
    },
    {
        "value": {
            "Konfigurationsparameter": "Top2"
        },
        "children": [
            {
                "value": {
                    "Konfigurationsparameter": "Level2 - 5"
                },
                "children": [
                    {
                        "value": {
                            "Konfigurationsparameter": "Level3 - 3"
                        },
                        "children": [
                        ]
                    }
                ]
            }
        ]
    },
    {
        "value": {
            "Konfigurationsparameter": "Top3"
        },
        "children": [
            {
                "value": {
                    "Konfigurationsparameter": "Level2 - 6"
                },
                "children": [
                ]
            }
        ]
    }
]
```
# configuration.activeitems
Wenn die Einstellung gesetzt wird, werden alle nicht aufgelisteten Menü Items deaktiviert.
Wird die Einstellung leer gelassen, sind alle Menü Items aktiv.
Die Konfiguration folgt folgendem Beispiel:
````
{
 "activeItems": ["statistic","configoverview"]
}
```
# configuration.delete.history.data
 Es wird hier eine Anzhal von Jahren verwendet z.B 5;
 ```
