# 💰 Budget Book
Budget Book ist ein schlanker, webbasierter Finanzmanager zur einfachen Kontrolle der Ausgaben und Einnahmen. Behalte dein Geld im Blick, kategorisiere deine Finanzen und plane smarter.

<img width="1512" height="765" alt="Bildschirmfoto 2026-04-29 um 11 52 54" src="https://github.com/user-attachments/assets/3551f816-4c37-401f-bf75-ed4b748b5e66" />

## 🚀 Features
- **Dashboard:** Alle wichtigen Kennzahlen auf einen Blick über eine intuitive Iframe-Struktur.
- **Transaktionsverwaltung:** Schnelles Hinzufügen und Filtern von Ausgaben/Einnahmen.
- **Kategorien:** Strukturierte Einteilung der Finanzen.
- **Intelligentes Warnsystem:** Erhalte visuelle Warnungen, wenn 90% des gesetzten Budgets erreicht sind oder der Saldo negativ ist.
- **User-Authentifizierung:** Sicherer Zugriff auf die Daten via LocalStorage (Token-basiert). Der Authentifizierungs-Flow leitet bei einem ungültigen/fehlenden Token zurück auf die welcome.html und bei einem gültigen Token zurück auf die homePage.html.
- **Responsive Tab-Navigation:** Schneller Wechsel zwischen den Modulen ohne lästige Ladezeiten.

## 🛠 Tech-Stack
- **Frontend:** HTML5: Struktur und Content-Layout, CSS3: Modularer Aufbau (Flexbox/Grid) & Tab-Styling, JavaScript: App-Logik, Authentifizierung & API-Interaktion
- **State Management:** LocalStorage API: Persistente Speicherung von User-Sessions (Tokens)
- **Backend:** Java Spring Boot: Zuständig für die Geschäftslogik, API-Endpunkte und die sichere Authentifizierung der Benutzer
- **Datenbank:** SQLite: Ein performantes, dateibasiertes Datenbanksystem zur zuverlässigen Speicherung von Transaktionsdaten und Benutzerprofilen

## 🏛️ Architektur
Dieses Architekturdiagramm stellt den schematischen Aufbau der Webanwendung dar, die auf einem Spring-Boot-Backend basiert. Es handelt sich um die klassische 3-Schichten-Architektur (Präsentationsschicht, Logikschicht, Datenschicht).

<img width="842" height="1264" alt="Benutzer" src="https://github.com/user-attachments/assets/b6188f2b-e492-4416-b0db-556d77f212a8" />

## 📁 Projektstruktur
```text
src/main/
├── java/                    # Backend Logik (Spring Boot Controller, Services, etc.)
        ├── api/             # REST-Controller für API-Endpunkte
        ├── dbaccess/        # Zugriffsschicht für Datenbank-Operationen (Repositories)
        ├── dbbackground/    # Hintergrundprozesse zur Datenwartung
        ├── dto/             # Daten-Transfer-Objekte für den Datenaustausch
        ├── haushaltsbuch/   # Kern-Geschäftslogik und Business-Services             
        ├── model/           # Datenbank-Entitäten und Datenmodelle
        ├── scheduled/       # Geplante Aufgaben und Cron-Jobs
        ├── security/        # Authentifizierungs- und Autorisierungs-Konfiguration
        ├── util/            # Hilfsklassen und allgemeine Werkzeuge
└── resources/
    └── static/              # Frontend (Assets & HTML)
        ├── js/              # JavaScript-Dateien
        ├── style/           # CSS Styles
        ├── icons/           # Bilddateien & Icons
        ├── categories.html  # HTML-Dateien
        ├── ...
```
## 🚀 Installation & Setup
Um das Projekt lokal zu nutzen, sind nur wenige Schritte erforderlich:

1.  **Repository klonen:**
    ```bash
    git clone https://github.com/yannismichel09/Haushaltsbuch.git
    ```
2.  **Ordner öffnen:**
    Öffne den geklonten Ordner in einer IDE, z.B. VS Code und starte die Spring Boot Anwendung
3.  **Dateien öffnen:**
    Sobald das Backend läuft, öffne einfach die `welcome.html` in deinem bevorzugten Browser.
4.  **Live-Server:**
    Für eine optimale Erfahrung mit den Iframes wird die Nutzung der VS Code Extension "Live Server" empfohlen.

## 📖 Benutzung
1. **Authentifizierung**  
Erstelle ein Konto oder logge dich ein.
<img width="1512" height="774" alt="Bildschirmfoto 2026-04-29 um 11 55 39" src="https://github.com/user-attachments/assets/46a9b1e4-f738-4365-8b23-40b0b6ff9455" />
<img width="1512" height="758" alt="Bildschirmfoto 2026-04-29 um 11 56 05" src="https://github.com/user-attachments/assets/7a374b5f-9389-4ca9-ad74-49a7b9a82794" />
<img width="1512" height="761" alt="Bildschirmfoto 2026-04-29 um 11 55 55" src="https://github.com/user-attachments/assets/1c52f475-5ca6-4a99-88b6-3c75a8cce5f7" />

2. **Navigation**  
Nutze die Tab-Leiste, um zwischen Dashboard, Transaktionen, Kategorien und Einstellungen zu wechseln.
<img width="1512" height="100" alt="Bildschirmfoto 2026-04-29 um 12 11 02" src="https://github.com/user-attachments/assets/ee47977a-81da-471c-b069-bdf69ba2c5e7" />

3. **Dashboard**  
Das Dashboard bietet eine zentrale Finanzübersicht: Im oberen Bereich werden Einnahmen, Ausgaben sowie der Saldo des aktuellen Monats kompakt dargestellt, ergänzt durch eine grafische Verlaufsanalyse der monatlichen Cashflows. Im unteren Abschnitt erhalten Nutzer zudem einen detaillierten Überblick über den Budgetverbrauch pro Kategorie. Der Bereich Suchen und Filtern ermöglicht das gezielte Finden und Analysieren einzelner Buchungen.
<img width="1512" height="724" alt="Bildschirmfoto 2026-04-29 um 11 59 24" src="https://github.com/user-attachments/assets/7ff5c805-5d48-4852-9637-1c89d3fc9ffc" />
<img width="1512" height="477" alt="Bildschirmfoto 2026-04-29 um 12 09 41" src="https://github.com/user-attachments/assets/1c2c3d70-99ab-4341-98f8-99fe1b87402a" />
<img width="1512" height="463" alt="Bildschirmfoto 2026-04-29 um 11 59 35" src="https://github.com/user-attachments/assets/b388ebda-5f10-4ad1-85ba-3026db50a1d6" />

4. **Transaktionen**  
Der Bereich Transaktionen dient als zentrale Buchungsübersicht. Hier können alle Einnahmen und Ausgaben detailliert eingesehen und verwaltet werden. Mithilfe intuitiver Such- und Filterfunktionen lassen sich Buchungen präzise nach Kategorien, Datumsbereichen oder Schlagworten eingrenzen, was eine gezielte Auswertung und Analyse der finanziellen Aktivitäten ermöglicht.
<img width="1512" height="693" alt="Bildschirmfoto 2026-04-29 um 11 59 55" src="https://github.com/user-attachments/assets/8881210e-75a9-452c-8b53-cd1585a9c33c" />
<img width="1512" height="400" alt="Bildschirmfoto 2026-04-29 um 12 00 06" src="https://github.com/user-attachments/assets/dc4d3bb3-2fc7-4fdd-992b-f45dc1a1da96" />

5. **Kategorien**  
Der Bereich Kategorien bildet das organisatorische Rückgrat der Anwendung. Hier können Nutzer ihre Finanzen in individuelle Gruppen unterteilen (z. B. Wohnen, Lebensmittel, Freizeit). Diese strukturierte Einteilung ermöglicht nicht nur eine klare Übersicht der Ausgaben, sondern bildet zudem die essenzielle Grundlage für das intelligente Warnsystem, das Budgets pro Kategorie präzise überwacht.
<img width="1512" height="729" alt="Bildschirmfoto 2026-04-29 um 12 00 51" src="https://github.com/user-attachments/assets/eb14023a-797d-4f5f-958f-893c09e2e919" />

6. **Einstellungen**  
Der Bereich Einstellungen dient als zentrale Anlaufstelle für die individuelle Konfiguration der Anwendung und ist übersichtlich in die zwei Unterbereiche Account und Sicherheit unterteilt. Hier können Nutzer ihr Benutzerprofil verwalten.
<img width="1512" height="642" alt="Bildschirmfoto 2026-04-29 um 12 01 18" src="https://github.com/user-attachments/assets/b9ec3d9d-271c-4ca3-b381-4f7fc6dd24e5" />
<img width="1512" height="669" alt="Bildschirmfoto 2026-04-29 um 12 01 38" src="https://github.com/user-attachments/assets/b71e4d28-9558-44d0-91b3-8497f5d31495" />

7. **Logout**  
Klicke auf dein Profilbild -> Logout, um die Session zu beenden.
<img width="225" height="126" alt="Bildschirmfoto 2026-04-29 um 12 23 15" src="https://github.com/user-attachments/assets/f49d19d4-4a42-4276-ab36-6ca0a59edd27" />

8. **Warnungen**  
Achte auf das Ausrufezeichen-Icon im Header. Es zeigt dir in Echtzeit an, wenn Kategorien (fast) dein definiertes Limit erreicht haben oder der Saldo negativ ist.
<img width="481" height="216" alt="Bildschirmfoto 2026-04-29 um 12 24 23" src="https://github.com/user-attachments/assets/85dfcb65-7290-4c74-9f46-06092a1c6726" />


## 📄 Lizenz & Kontakt  
Dieses Projekt wurde zu Lernzwecken und als Projektarbeit (Studienprojekt) zur Webentwicklung erstellt.  
Autor 1: [Yannis Michel/yannismichel09]  
Autor 2: [Said Resch/Papierjaeger]
