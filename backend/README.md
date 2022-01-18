# Dependency Injection im Backend
Um Wartbarkeit und Testbarkeit des Backends zu ermöglichen, sollten keine Singletons verwendet werden. 
Diese sind zwar auch über Umwege mit Reflections testbar, können aber trotzdem Nebeneffekte herbeiführen. 
Stattdessen werden Dependencies über Dependency Injection zur Verfügung gestellt. Dies macht Testen einfacher 
und macht es auch in Methodensignaturen und Konstruktoren sichtbar, welche Abhängigkeiten zwischen
Klassen existieren.

## DI in der Produktion
Um Klassen injectable zu machen, müssen sie neben einer passenden Annotation (z. B. @Singleton)
in `de.jsmenues.backend.BackendApplicationBinder` per `bind(...)` an eine Klasse gebunden werden. 
Ansonsten ist die Klasse für das DI-Framework nicht auffindbar und es wird zur Laufzeit eine
Exception geworfen.

## DI beim Testen
Beim Testen können Dependencies per Konstruktor hereingegeben werden. Dies ist sinnvoll, wenn nicht
innerhalb eines `JerseyTest`s getestet werden muss, also keine REST-Endpunkte getestet werden.
Um mit DI in einem `JerseyTest` zu testen, muss in `configure()` ein `AbstractBinder` erstellt werden,
der alle benötigten zu injizierenden Dependencies `bind`et. Wenn eine `ResourceConfig` verwendet
wird, welche bereits eine Dependency eines Typs deklariert, muss innerhalb des `AbstractBinder`s
mit `ranked(...)` die Priorität überschrieben werden.
Auch muss bei zugriffsgeschützten Endpunkten eventuell eine Implementierung von `AuthenticationFilter`
zur Verfügung gestellt werden, um keine Zugriffskontrolle beim Testen durchzuführen.