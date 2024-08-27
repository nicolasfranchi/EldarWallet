# EldarWallet

Esta aplicación permite a los usuarios gestionar sus tarjetas, realizar pagos mediante QR y ofrece la funcionalidad de pagos a través de NFC (pendiente de implementación).

## Características

- **Inicio de Sesión:** Autenticación de usuario mediante email y contraseña utilizando Firebase Authentication.
- **Gestión de Tarjetas:** Agregar, visualizar y eliminar tarjetas de crédito. La aplicación detecta automáticamente el emisor de la tarjeta (Visa, Mastercard, American Express) basado en el primer dígito del número de tarjeta.
- **Pago con QR:** Escanea códigos QR para realizar pagos rápidamente.
- **Soporte para Pagos NFC:** Planificación para futuras implementaciones de pagos mediante NFC.

## Tecnologías Utilizadas

- **Lenguaje:** Kotlin
- **Arquitectura:** MVVM (Model-View-ViewModel)
- **Autenticación:** Firebase Authentication
- **Almacenamiento:** Firebase Firestore
- **Interfaz de Usuario:** Material Design y componentes de Jetpack

## Configuración del Proyecto

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/nicolasfranchi/EldarWallet.git
   ```
   
2. **Abrir el proyecto en Android Studio:** 
   - Asegúrate de tener instalada la última versión de Android Studio.
   - Importa el proyecto y deja que Android Studio descargue todas las dependencias necesarias.

3. **Configurar Firebase:**
   - Ve a la consola de Firebase y crea un nuevo proyecto.
   - Agrega la aplicación Android al proyecto de Firebase y sigue las instrucciones para agregar el archivo `google-services.json` a tu aplicación.
   - Habilita Firebase Authentication y Firestore en la consola de Firebase.

4. **Ejecutar la aplicación:**
   - Conecta un dispositivo Android o utiliza un emulador.
   - Ejecuta la aplicación desde Android Studio.

## Estructura del Proyecto

- **`MainActivity`**: Pantalla principal que muestra el saldo y las tarjetas del usuario.
- **`AddCardActivity`**: Permite al usuario agregar una nueva tarjeta de crédito.
- **`PayActivity`**: Pantalla para realizar pagos mediante QR.
- **`QRCodeViewModel`**: Maneja la lógica de negocios y la interacción con el repositorio para las operaciones de QR.
- **`MainViewModel`**: Gestiona la carga y actualización de los datos de las tarjetas de crédito del usuario.

## Consideraciones Técnicas

- **Seguridad de los Datos:** La información de las tarjetas se almacena de manera encriptada en la base de datos interna.
- **Monitoreo del Estado de la Aplicación:** Se utiliza LiveData para la gestión de datos en tiempo real y la actualización de la interfaz de usuario.
