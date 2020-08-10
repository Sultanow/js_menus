# Konzept für Newstweets
## Verwendungszwecke
Mit den Newstweets können Informationen an Projektmitarbeiter schnell und unkompliziert verbreitet werden ohne die überflüssigen Daten, welche in einer Mail mitgeschickt werden. Hierbei handelt es sich um Kurzinformationen. Für Beispielsweise Informationen zu einem Softwareupdate oder auch zu geplanten Releases.

## Was wird benötigt
Es werden Änderungen im Backend und im Frontend benötigt. Sowie eine Schnittstelle für die Kommunikation zwischen diesen. Im Frontend ist eine Sidebar geplant, in der man die Tweets anzeigen lassen kann. Zusätzlich ist ein Admin-Panel geplant, in dem die Nachrichten bearbeitet und auch gelöscht werden können von einem Content-Admin.

### Frontend
Im Startbildschirm auf der rechten Seite soll es ein Icon geben, welches eine Sidebar auf der Rechten Seite öffnet. Zusätzlich sollen die Tweets über das 'News' Menü erreichbar sein. Im News-Menü wird es zusätzlich die Möglichkeit geben die News zu filtern. Eine Filterung erfolgt über 'Channels', welche mit Hashtag definiert sind. Desweiteren gibt es die Möglichkeit hier in die Content-Admin-Ansicht zu wechseln. In dieser Ansicht ist es möglich Tweets zu bearbeiten und zu löschen. Zusätzlich soll es möglich sein Tweets auszublenden, um sie Temporär nicht anzuzeigen. Das erstellen von Tweets ist im ersten Schritt nur in der Content-Admin-Ansicht möglich.

### Schnittstelle
Beim Erstellen der Tweets aus dem Frontend sollen folgende Daten an das Backend gehen:
```
{
    titel: "Titel",
    text: "Text",
}
```
Daraufhin wird der Tweet im Backend angelegt und in Redis gespeichert.

Wenn Tweets an das Frontend geschickt werden, so sieht das wie folgt aus:
```
{
    titel: "Titel",
    text: "Text",
    id: <Nummer>,
    date: <Datum als String>,
    ausgeblendet: false,
    priority: "low"|"normal"(Default)|"high",
}
```

### Backend
Im Backend werden die Tweets in Redis gespeichert. Für die Speicherung wird jedem Tweet eine ID gepeichert. 
Die Speicherung erfolgt dabei im Objekt `NewsItem`. Dies wird unter dem RedisKey `news.item.<id>` abgelegt.
Zusätzlich wird der Text geparst nach Hashtags. Hashtags werden wie Channels behandelt. Die Id des Tweets wird dann in die Liste des Redis Eintrags geschrieben. Channels werden immer als Lowercase betrachtet.
```
news.channel.<channelName>
```
Es wird zusätzlich immer die letzte vergebene Id gespeichert im Key `news.lastId`.
