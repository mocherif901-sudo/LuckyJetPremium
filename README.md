# Lucky Jet Premium — projet Android

Application Android native en Java, thème bleu nuit et or.

## Fonctionnalités incluses
- Horloge en temps réel sur le fuseau `Africa/Abidjan` (GMT).
- Compte à rebours de deux minutes à partir du bouton.
- Heure cible affichée.
- Historique local des six dernières sessions (en mémoire pendant l'exécution).
- Interface mobile simple, sans permission ni connexion à un compte de jeu.

## Limite importante
Ce projet ne prédit pas le multiplicateur du prochain tour. Lucky Jet est un jeu de hasard et l'application n'accède pas au serveur du jeu. À la fin du compte à rebours, elle indique explicitement que le résultat réel reste inconnu. N'utilisez pas une estimation comme garantie de gain.

## Générer l'APK avec Android Studio
1. Installez Android Studio et le SDK Android 35.
2. Décompressez le ZIP.
3. Dans Android Studio, choisissez **Open** et sélectionnez le dossier `LuckyJetPremium`.
4. Attendez la synchronisation Gradle.
5. Ouvrez **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. L'APK de test sera normalement créé dans `app/build/outputs/apk/debug/app-debug.apk`.

Configuration requise : Android Studio récent, JDK 17 et connexion Internet au premier téléchargement des composants Gradle/SDK.
