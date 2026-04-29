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
/
├── js/
│   ├── apiUser.js          # Authentifizierungs-Logik
│   ├── apiWarnings.js      # API-Anbindung für Warnungen
│   ├── global.js           # Globale Funktionen
│   ├── homePageIframes.js  # Steuerung der Iframes
│   ├── homePageWarnings.js # Warnungs-Logik
│   └── tabs.js             # Tab-Navigation
├── style/
│   ├── homePage.css        # Styles für die Hauptseite
│   └── tabs.css            # Styles für die Navigation
├── welcome.html            # Login & Registrierung
├── homePage.html           # Hauptapplikation
└── dashboard.html/etc.     # Unterseiten der App
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

5. **Logout**
Klicke auf dein Profilbild -> Logout, um die Session zu beenden.
6. **Warnungen**
Achte auf das Ausrufezeichen-Icon im Header. Es zeigt dir in Echtzeit an, wenn Kategorien (fast) dein definiertes Limit erreicht haben oder der Saldo negativ ist.

## 📄 Lizenz & Kontakt
Dieses Projekt wurde zu Lernzwecken und als Projektarbeit (Studienprojekt) zur Webentwicklung erstellt.
Autor 1: [Yannis Michel/yannismichel09]
Autor 2: [Said Resch/GitHub-Username]
