# Lab 12 — Localisation Temps Réel GPS + Google Maps

## Présentation

Application Android complète combinant GPS temps réel, envoi des coordonnées vers un backend PHP/MySQL via Volley, et affichage des positions sur Google Maps avec markers.

---

## Structure du projet

### Backend PHP (localisation/)
| Fichier | Rôle |
|---------|------|
| classe/Position.php | Modèle métier |
| connexion/Connexion.php | Connexion PDO MySQL |
| dao/IDao.php | Interface CRUD |
| service/PositionService.php | Logique accès données |
| createPosition.php | API insertion POST |
| showPositions.php | API récupération JSON |

### Android (LocalisationTempsReel/)
| Fichier | Rôle |
|---------|------|
| MainActivity.java | GPS + envoi Volley |
| MapsActivity.java | Affichage markers Google Maps |
| activity_main.xml | Interface principale |
| activity_maps.xml | Interface carte |
| AndroidManifest.xml | Permissions + clé API |

---

## Base de données MySQL

```sql
CREATE DATABASE IF NOT EXISTS localisation;

CREATE TABLE position (
    id INT AUTO_INCREMENT PRIMARY KEY,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    date DATETIME NOT NULL,
    imei VARCHAR(50) NOT NULL
);
```

---

## Fonctionnalités

| Fonctionnalité | Description |
|---|---|
| GPS temps réel | Suivi position via GPS_PROVIDER |
| Envoi serveur | POST vers createPosition.php |
| Affichage carte | Markers sur Google Maps |
| showPositions | Récupération JSON des positions |
| Permission runtime | ACCESS_FINE_LOCATION |

---

## Architecture
Android (GPS) → Volley POST → createPosition.php → MySQL
Android (Maps) → Volley POST → showPositions.php → JSON → Markers

---

## Personnalisation HC

- MainActivity : `hcInsertUrl`, `hcRequestQueue`, `hcLocationManager`
- MapsActivity : `hcMap`, `hcShowUrl`, `hcLoadPositions()`
- Palette : vert foncé / orange

---

## Démonstration


https://github.com/user-attachments/assets/d30769c2-a909-44f5-a28e-f3f512f5e759


---

## Technologies utilisées
- Android Studio
- Java
- Google Maps SDK for Android
- LocationManager / GPS
- Volley
- PHP 8 / PDO
- MySQL
- XAMPP
- MaterialComponents
