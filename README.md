# 💰 Budget Book
Budget Book ist ein schlanker, webbasierter Finanzmanager zur einfachen Kontrolle der Ausgaben und Einnahmen. Behalte dein Geld im Blick, kategorisiere deine Finanzen und plane smarter.

## 🚀 Features
- **Dashboard:** Alle wichtigen Kennzahlen auf einen Blick über eine intuitive Iframe-Struktur.
- **Transaktionsverwaltung:** Schnelles Hinzufügen und Filtern von Ausgaben/Einnahmen.
- **Kategorien:** Strukturierte Einteilung der Finanzen.
- **Intelligentes Warnsystem:** Erhalte visuelle Warnungen, wenn 90% des gesetzten Budgets erreicht sind und der Saldo negativ ist.
- **User-Authentifizierung:** Sicherer Zugriff auf die Daten via LocalStorage und Token-basiert.
- **Responsive Tab-Navigation:** Schneller Wechsel zwischen den Modulen ohne lästige Ladezeiten.

## 🛠 Tech-Stack
- **Frontend:** HTML5: Struktur und Content-Layout, CSS3: Modularer Aufbau (Flexbox/Grid) & Tab-Styling, JavaScript: App-Logik, Authentifizierung & API-Interaktion
- **State Management:** LocalStorage API: Persistente Speicherung von User-Sessions (Tokens)
- **Backend:** Java Spring Boot
- **Datenbank:** SQLite

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
    git clone [https://github.com/DEIN-USERNAME/budget-book.git](https://github.com/DEIN-USERNAME/budget-book.git)
    ```
2.  **Dateien öffnen:**
    Öffne den geklonten Ordner in einer IDE z.B. VS Code und starte die Spring Boot Anwendung
3.  **Dateien öffnen:**
    Da es sich um eine reine Frontend-Applikation handelt, musst du keine Pakete installieren. Öffne einfach die `welcome.html` in   deinem bevorzugten Browser.
4.  **Live-Server:**
    Für eine optimale Erfahrung mit den Iframes wird die Nutzung der VS Code Extension "Live Server" empfohlen.
