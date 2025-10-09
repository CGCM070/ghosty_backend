# Sistema de Manejo de Excepciones - Ghosty Backend

## 📋 Descripción General

Este documento describe el sistema completo de manejo de excepciones implementado en el backend de Ghosty. El sistema captura automáticamente todas las excepciones y devuelve respuestas JSON estandarizadas que facilitan las pruebas en Postman y la depuración.

## 🎯 Excepciones Personalizadas

Se han creado las siguientes excepciones personalizadas:

### 1. `ResourceNotFoundException`
- **Uso**: Cuando no se encuentra un recurso solicitado (usuario, rol, etc.)
- **Código HTTP**: 404 NOT FOUND
- **Ejemplo**:
```java
throw new ResourceNotFoundException("Usuario con id " + id + " no encontrado");
```

### 2. `BadRequestException`
- **Uso**: Cuando la solicitud contiene datos incorrectos o inválidos
- **Código HTTP**: 400 BAD REQUEST
- **Ejemplo**:
```java
throw new BadRequestException("El formato del email es inválido");
```

### 3. `ConflictException`
- **Uso**: Cuando hay conflictos (como emails duplicados)
- **Código HTTP**: 409 CONFLICT
- **Ejemplo**:
```java
throw new ConflictException("El email ya está registrado");
```

### 4. `UnauthorizedException`
- **Uso**: Cuando hay problemas de autenticación
- **Código HTTP**: 401 UNAUTHORIZED
- **Ejemplo**:
```java
throw new UnauthorizedException("Token inválido o expirado");
```

## 🔧 Excepciones del Sistema Capturadas

El `GeneralControllerAdvice` captura automáticamente las siguientes excepciones:

### 1. Validación de Campos (`MethodArgumentNotValidException`)
- Captura errores de validación de `@Valid`, `@NotBlank`, `@Email`, etc.
- Devuelve un mapa con todos los campos inválidos y sus mensajes de error

**Ejemplo de respuesta**:
```json
{
  "timestamp": "2024-01-09T17:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación en los campos proporcionados",
  "path": "/v1/api/auth/register",
  "validationErrors": {
    "email": "must not be blank",
    "username": "must not be blank"
  }
}
```

### 2. Credenciales Incorrectas (`BadCredentialsException`)
- Captura errores de autenticación de Spring Security
- Código HTTP: 401

### 3. Acceso Denegado (`AccessDeniedException`)
- Captura intentos de acceso sin permisos suficientes
- Código HTTP: 403 FORBIDDEN

### 4. Tokens JWT
- `ExpiredJwtException`: Token expirado
- `MalformedJwtException`: Token malformado
- `SignatureException`: Firma del token inválida
- Código HTTP: 401

### 5. Integridad de Datos (`DataIntegrityViolationException`)
- Captura violaciones de constraints de base de datos
- Detecta automáticamente duplicados y problemas de foreign keys
- Código HTTP: 409 CONFLICT

### 6. Tipo de Argumento Incorrecto (`MethodArgumentTypeMismatchException`)
- Captura cuando se envía un tipo de dato incorrecto (ej: string en lugar de número)
- Código HTTP: 400

### 7. JSON Malformado (`HttpMessageNotReadableException`)
- Captura errores de parsing de JSON
- Código HTTP: 400

### 8. Excepciones Generales
- `RuntimeException`: Excepciones de runtime no específicas
- `Exception`: Cualquier otra excepción no contemplada
- Código HTTP: 500 INTERNAL SERVER ERROR

## 📝 Formato de Respuesta Estándar

Todas las excepciones devuelven una respuesta con el siguiente formato:

```json
{
  "timestamp": "2024-01-09T17:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Usuario con id 123 no encontrado",
  "path": "/v1/api/users/123",
  "validationErrors": null
}
```

### Campos:
- **timestamp**: Momento exacto en que ocurrió el error
- **status**: Código HTTP del error
- **error**: Nombre del estado HTTP
- **message**: Mensaje descriptivo del error
- **path**: Ruta del endpoint donde ocurrió el error
- **validationErrors**: Mapa de errores de validación (solo para errores de validación)

## 🧪 Ejemplos de Pruebas en Postman

### 1. Probar Validación de Campos
**Request**: POST `/v1/api/auth/register`
```json
{
  "username": "",
  "email": "invalid-email",
  "password": ""
}
```
**Response esperada**: 400 con validationErrors

### 2. Probar Usuario No Encontrado
**Request**: GET `/v1/api/users/99999`
**Response esperada**: 404 con mensaje "Usuario con id 99999 no encontrado"

### 3. Probar Email Duplicado
**Request**: POST `/v1/api/auth/register` con un email ya registrado
**Response esperada**: 409 con mensaje "El email ya está registrado"

### 4. Probar Credenciales Inválidas
**Request**: POST `/v1/api/auth/login`
```json
{
  "email": "test@test.com",
  "password": "wrong-password"
}
```
**Response esperada**: 401 con mensaje "Credenciales inválidas"

### 5. Probar Token Expirado/Inválido
**Request**: GET `/v1/api/users` con token JWT inválido en header
**Response esperada**: 401 con mensaje sobre token inválido

### 6. Probar JSON Malformado
**Request**: POST `/v1/api/auth/register` con JSON mal formado
```
{
  "username": "test"
  "email": "test@test.com"  // falta la coma
}
```
**Response esperada**: 400 con mensaje sobre JSON malformado

### 7. Probar Tipo de Dato Incorrecto
**Request**: GET `/v1/api/users/abc` (enviando string en lugar de número)
**Response esperada**: 400 con mensaje sobre tipo de parámetro inválido

## 🚀 Beneficios

1. **Respuestas Consistentes**: Todas las excepciones siguen el mismo formato
2. **Mejor Debugging**: Mensajes claros en español sobre qué salió mal
3. **Fácil Testing**: Respuestas predecibles facilitan las pruebas en Postman
4. **Códigos HTTP Correctos**: Cada tipo de error usa el código HTTP apropiado
5. **Información Detallada**: El campo `validationErrors` muestra exactamente qué campos son inválidos
6. **Rastreable**: El campo `path` muestra en qué endpoint ocurrió el error

## 📚 Cómo Usar en el Código

### Ejemplo 1: Lanzar excepción cuando no se encuentra un recurso
```java
public UserResponseDTO findById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + id + " no encontrado"));
    // ... resto del código
}
```

### Ejemplo 2: Lanzar excepción de conflicto
```java
public void registerUser(RegisterRequestDTO dto) {
    if (userRepository.findByEmail(dto.email()).isPresent()) {
        throw new ConflictException("El email ya está registrado");
    }
    // ... resto del código
}
```

### Ejemplo 3: Validación automática con @Valid
```java
@PostMapping("/register")
public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
    // Si los datos no son válidos, el @Valid automáticamente lanza MethodArgumentNotValidException
    // que es capturada por el GeneralControllerAdvice
    return ResponseEntity.ok(authService.register(request));
}
```

## ✅ Cambios Realizados

1. ✅ Creadas 4 excepciones personalizadas
2. ✅ Creado `ErrorResponseDTO` para respuestas estandarizadas
3. ✅ Implementado `GeneralControllerAdvice` con 15 manejadores de excepciones
4. ✅ Actualizado `AuthenticationService` para usar las nuevas excepciones
5. ✅ Actualizado `UserService` para usar las nuevas excepciones
6. ✅ Todos los mensajes de error traducidos al español
7. ✅ Proyecto compilado exitosamente

## 🎨 Recomendaciones

1. **Usa las excepciones personalizadas** en lugar de `RuntimeException` genérica
2. **Proporciona mensajes descriptivos** que ayuden a entender el problema
3. **Valida en los DTOs** usando anotaciones como `@NotBlank`, `@Email`, etc.
4. **Prueba todos los casos de error** en Postman para verificar las respuestas
5. **No expongas información sensible** en los mensajes de error (como passwords o tokens)

