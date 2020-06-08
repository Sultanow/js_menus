param(
    [string]$start = "all"
)

$redisDbAvailable = $false
$zabbixDbAvailable = $false
# First check if databases available
if (Test-Path -Path .\docker\redis\data\appendonly.aof) {
    $redisDbAvailable = $true
}

if (Test-Path -Path .\docker\zabbix\zbx_env\) {
    $zabbixDbAvailable = $true
}

$homePath = $PWD.Path



if (($start -eq "all") -or ($start -eq "zabbix")) {
    $zabbixPath = "$($homePath)\docker\zabbix"
    Set-Location -Path $zabbixPath
    Write-Output "Zabbix Service starten"
    docker-compose.exe up -d 
    Write-Output "Zabbix gestartet... Vollständiger Start dauert ca 2 Minuten"
    if (!($zabbixDbAvailable)) {
        Set-Location -Path $homePath
        Write-Output "Warte 120 sec um die Zabbix-Datenbank zu füllen."
        Start-Sleep 120
        Write-Output "Starten des Dockercontainers um default Werte zu laden"
        docker build -f $PWD\docker\zabbix\scripts\Dockerfile-pythonZabbixInit -t "pythonzabbix" .
        docker run -it --rm -v $PWD\docker\zabbix\scripts:/usr/src/app/ --network "cockpit-net" pythonzabbix createDefaultConfig.py "http://zabbix-frontend:80"
        Write-Output "Werte sollten eingetragen sein..."
    }
    Set-Location -Path $homePath
}



if (($start -eq "all") -or ($start -eq "jsmenu")) {
    $jsmenuPath = "$($homePath)\docker"
    Set-Location -Path $jsmenuPath
    Write-Output "Config-Cockpit starten"
    docker-compose.exe up -d
    Write-Output "Config-Cockpit Umgebung gestartet. Der Startup dauert ca 2 Minuten."
    if(!($redisDbAvailable)) {
        Write-Output "Für den Fall des ersten Startens müssen die Konfigurationen noch gesetzt werden. Diese könnnen unter /docs/parameterbeschreibung.md nachgelesen werden. Das Standardpasswort für die Settings ist '1234'." 
    }
    Write-Output "Das Cockpit ist unter http://localhost:8080 erreichbar."
    Set-Location -Path $homePath
}


