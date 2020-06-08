param(
    [int]$force = 0,
    [string]$build = "all"
)

function checkNameExistsInArray {
    param (
        $array, $checkString
    )
    $checkString = ""
    $checkString = $array[1]
    if ($checkString -eq "") {
        return 0
    }
    foreach ($element in $array[0]) {
        if ($element -match $checkString) {
            return 1
        }
    }
    return 0
}

function checkContainerExists {
    param(
        [int]$force = 0,
        [string]$containerName,
        [string]$readableContainerName,
        $allInstalledImages,
        [string]$dockerfile
    )
    $output = checkNameExistsInArray($allInstalledImages, $containerName)
    if (($output -eq 0) -or ($force -eq 1)) {
        Write-Output "$($readableContainerName) ist nicht vorhanden oder Parameter 'force' ist gesetzt => Bauen des Images"
        docker build -f $dockerfile -t $containerName ../../
    }
    else {
        Write-Output "$($readableContainerName) ist vorhanden, kein erneutes Bauen notwendig."
    }
}

if ($PWD.Path -notmatch "docker\\builder") {
    Write-Output $PWD.Path
    Write-Output "Zur ausführung in das Verzeichnis /docker/builder wechseln!"
    exit 1
}

$allInstalledImages = ""
$allInstalledImages = docker images

checkContainerExists $force "mavenbuilder" "Maven-Build Image" $allInstalledImages "./Dockerfile-mvn"
checkContainerExists $force "backendbuilder" "Backend-Build Image" $allInstalledImages "./Dockerfile-backend"
checkContainerExists $force "frontendbuilder" "Frontend-Build Image" $allInstalledImages "./Dockerfile-frontend"
checkContainerExists $force "frontendwarbuilder" "Frontend-War-Build Image" $allInstalledImages "./Dockerfile-frontend-war"



$destinationFolder = -join ($PWD.Path, '\..\..\target\autodeploy\')
if (!(Test-Path -path $destinationFolder)) { New-Item $destinationFolder -Type Directory }
$erfolg = "erfolgreich"

## Docker Container build done, now build the war files.
if ($build -eq "all" -or $build -eq "backend") {
    # Build the backend
    docker run -it --rm -v $PWD\..\..\backend\target:/usr/src/app/target -v $PWD\..\..\backend\src:/usr/src/app/src backendbuilder clean install -DbuildDirectory='${project.basedir}/target/'
    if (Test-Path -Path .\..\..\backend\target\backend.war) {
        $srcPath = -join ($PWD.Path, '\..\..\backend\target\backend.war')
        $destPath = -join ($destinationFolder, 'backend.war')
        Copy-Item -Path $srcPath -Destination $destPath -Force
        Write-Output "backend.war erfolgreich in den autodeploy Ordner kopiert"
    }
    else {
        Write-Output "Build war nicht erfolgreich."
        $erfolg = "nicht"
    }
}

if ($build -eq "all" -or $build -eq "frontend") {
    docker run -it --rm -v $PWD\..\..\frontend\target\angular-radial-menu:/usr/src/target/angular/angular-radial-menu -v $PWD\..\..\angular\src:/usr/src/app/src frontendbuilder run buildProduction
    docker run -it --rm -v $PWD\..\..\frontend\target\angular-radial-menu:/usr/src/target/angular/angular-radial-menu -v $PWD\..\..\frontend\target:/usr/src/target/autodeploy frontendwarbuilder clean install
    if (Test-Path -Path .\..\..\frontend\target\frontend.war) {
        $srcPath = -join ($PWD.Path, '\..\..\frontend\target\frontend.war')
        $destPath = -join ($destinationFolder, 'frontend.war')
        Copy-Item -Path $srcPath -Destination $destPath -Force
        Write-Output "frontend.war erfolgreich in den autodeploy Ordner kopiert" 
    }
    else {
        Write-Output "Build war nicht erfolgreich."
        $erfolg = "nicht"
    }
}

Write-Output "Skriptende - Build wurde $($erfolg) erledigt."
