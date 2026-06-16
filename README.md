# Projekt zaliczeniowy  
## Conference Rooms Reservation API

Projekt zaliczeniowy przedstawiający system rezerwacji sal konferencyjnych.

---

## Użytkownicy

### Administrator
```powershell
$pair = "admin@yourfurniture.pl:Admin123!"
$bytes = [System.Text.Encoding]::ASCII.GetBytes($pair)
$base64Admin = [Convert]::ToBase64String($bytes)
```

### Użytkownik
```powershell
$pair = "jan.kowalski@yourfurniture.pl:User123!"
$bytes = [System.Text.Encoding]::ASCII.GetBytes($pair)
$base64User = [Convert]::ToBase64String($bytes)
```

### Błędne dane logowania
```powershell
$pair = "jan.kowalski@yourfurniture.pl:User123"
$bytes = [System.Text.Encoding]::ASCII.GetBytes($pair)
$base64Bad = [Convert]::ToBase64String($bytes)
```

---

# Users

## Endpoint: `GET /users/me`

Zwraca dane aktualnie zalogowanego użytkownika.

### Administrator
**Oczekiwany kod odpowiedzi:** `200 OK`  
**Opis:** Dane aktualnie zalogowanego użytkownika.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/users/me" `
  -Method GET `
  -Headers @{ Authorization = "Basic $base64Admin" }

$response.Content
```

### Użytkownik
**Oczekiwany kod odpowiedzi:** `200 OK`  
**Opis:** Dane aktualnie zalogowanego użytkownika.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/users/me" `
  -Method GET `
  -Headers @{ Authorization = "Basic $base64User" }

$response.Content
```

### Błędny użytkownik
**Oczekiwany kod odpowiedzi:** `401 Unauthorized`  
**Opis:** Brak uwierzytelnienia lub niepoprawne dane logowania.

```powershell
try {
    Invoke-WebRequest `
      -Uri "http://localhost:8080/users/me" `
      -Method GET `
      -Headers @{ Authorization = "Basic $base64Bad" }
} catch {
    $_.Exception.Response.StatusCode.value__
}
```


---

## Endpoint: `GET /users/me/reservations`

Zwraca listę rezerwacji aktualnie zalogowanego użytkownika.

### Administrator
**Oczekiwany kod odpowiedzi:** `200 OK`  
**Opis:** Lista rezerwacji aktualnie zalogowanego użytkownika.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/users/me/reservations" `
  -Method GET `
  -Headers @{ Authorization = "Basic $base64Admin" }

$response.Content
```

### Użytkownik
**Oczekiwany kod odpowiedzi:** `200 OK`  
**Opis:** Lista rezerwacji aktualnie zalogowanego użytkownika.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/users/me/reservations" `
  -Method GET `
  -Headers @{ Authorization = "Basic $base64User" }

$response.Content
```

### Błędny użytkownik
**Oczekiwany kod odpowiedzi:** `401 Unauthorized`  
**Opis:** Brak uwierzytelnienia lub niepoprawne dane logowania.

```powershell
try {
    Invoke-WebRequest `
      -Uri "http://localhost:8080/users/me/reservations" `
      -Method GET `
      -Headers @{ Authorization = "Basic $base64Bad" }
} catch {
    $_.Exception.Response.StatusCode.value__
}
```

---

# Rooms

## Endpoint: `GET /rooms`

Zwraca listę wszystkich sal konferencyjnych.

### Administrator
**Oczekiwany kod odpowiedzi:** `200 OK`  
**Opis:** Lista sal konferencyjnych.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/rooms" `
  -Method GET `
  -Headers @{ Authorization = "Basic $base64Admin" }

$response.Content
```

### Użytkownik
**Oczekiwany kod odpowiedzi:** `200 OK`  
**Opis:** Lista sal konferencyjnych.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/rooms" `
  -Method GET `
  -Headers @{ Authorization = "Basic $base64User" }

$response.Content
```

### Błędny użytkownik
**Oczekiwany kod odpowiedzi:** `401 Unauthorized`  
**Opis:** Brak uwierzytelnienia lub niepoprawne dane logowania.

```powershell
try {
    Invoke-WebRequest `
      -Uri "http://localhost:8080/rooms" `
      -Method GET `
      -Headers @{ Authorization = "Basic $base64Bad" }
} catch {
    $_.Exception.Response.StatusCode.value__
}
```
---

## Endpoint: `POST /rooms`

Dodaje nową salę konferencyjną. Endpoint dostępny tylko dla administratora.

### Administrator — sukces
**Oczekiwany kod odpowiedzi:** `201 Created`  
**Opis:** Sala konferencyjna została dodana.

```powershell
$body = @"
{
  "requestHeader": {
    "requestId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
    "sendDate": "2026-06-15T10:00:00Z"
  },
  "room": {
    "name": "Sala Konferencyjna Z",
    "capacity": 13,
    "location": "Budynek Z, piętro 1",
    "floor": 1,
    "status": "AVAILABLE",
    "hasProjector": true,
    "hasWhiteboard": true,
    "hasDesktopComputer": false,
    "hasSpeakers": true,
    "hasVideoConferenceSystem": true
  }
}
"@

$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/rooms" `
  -Method POST `
  -Headers @{
    Authorization = "Basic $base64Admin"
    "Content-Type" = "application/json"
  } `
  -Body $body

$response.Content
```

### Administrator — błędne dane wejściowe
**Oczekiwany kod odpowiedzi:** `400 Bad Request`  
**Opis:** Niepoprawne dane wejściowe.

```powershell
$body = @"
{
  "requestHeader": {
    "requestId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
    "sendDate": "2026-06-15T10:00:00Z"
  },
  "room": {
    "name": "Sala Konferencyjna Z",
    "capacity": 13,
    "floor": 1,
    "status": "AVAILABLE",
    "hasProjector": true,
    "hasWhiteboard": true,
    "hasDesktopComputer": false,
    "hasSpeakers": true,
    "hasVideoConferenceSystem": true
  }
}
"@

$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/rooms" `
  -Method POST `
  -Headers @{
    Authorization = "Basic $base64Admin"
    "Content-Type" = "application/json"
  } `
  -Body $body

$response.Content
```

### Administrator — konflikt danych
**Oczekiwany kod odpowiedzi:** `409 Conflict`  
**Opis:** Dodajemy salę o tej samej nazwie.

```powershell
$body = @"
{
  "requestHeader": {
    "requestId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
    "sendDate": "2026-06-15T10:00:00Z"
  },
  "room": {
    "name": "Sala Konferencyjna A",
    "capacity": 1,
    "location": "Budynek Z, piętro 1",
    "floor": 10,
    "status": "AVAILABLE",
    "hasProjector": true,
    "hasWhiteboard": true,
    "hasDesktopComputer": false,
    "hasSpeakers": true,
    "hasVideoConferenceSystem": true
  }
}
"@

$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/rooms" `
  -Method POST `
  -Headers @{
    Authorization = "Basic $base64Admin"
    "Content-Type" = "application/json"
  } `
  -Body $body
```

### Użytkownik
**Oczekiwany kod odpowiedzi:** `403 Forbidden`  
**Opis:** Brak uprawnień - operacja dostępna tylko dla ADMIN.

```powershell
$body = @"
{
  "requestHeader": {
    "requestId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
    "sendDate": "2026-06-15T10:00:00Z"
  },
  "room": {
    "name": "Sala Konferencyjna Z",
    "capacity": 13,
    "location": "Budynek Z, piętro 1",
    "floor": 1,
    "status": "AVAILABLE",
    "hasProjector": true,
    "hasWhiteboard": true,
    "hasDesktopComputer": false,
    "hasSpeakers": true,
    "hasVideoConferenceSystem": true
  }
}
"@

try {
  Invoke-WebRequest `
    -Uri "http://localhost:8080/rooms" `
    -Method POST `
    -Headers @{
      Authorization = "Basic $base64User"
      "Content-Type" = "application/json"
    } `
    -Body $body
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

### Błędny użytkownik
**Oczekiwany kod odpowiedzi:** `401 Unauthorized`  
**Opis:** Brak uwierzytelnienia lub niepoprawne dane logowania.

```powershell
$body = @"
{
  "requestHeader": {
    "requestId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
    "sendDate": "2026-06-15T10:00:00Z"
  },
  "room": {
    "name": "Sala Konferencyjna Z",
    "capacity": 13,
    "location": "Budynek Z, piętro 1",
    "floor": 1,
    "status": "AVAILABLE",
    "hasProjector": true,
    "hasWhiteboard": true,
    "hasDesktopComputer": false,
    "hasSpeakers": true,
    "hasVideoConferenceSystem": true
  }
}
"@

try {
  Invoke-WebRequest `
    -Uri "http://localhost:8080/rooms" `
    -Method POST `
    -Headers @{
      Authorization = "Basic $base64Bad"
      "Content-Type" = "application/json"
    } `
    -Body $body
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

---

## Endpoint: `GET /rooms/{id}`

Zwraca szczegóły wybranej sali konferencyjnej.

### Administrator — sukces
**Oczekiwany kod odpowiedzi:** `200 OK`  
**Opis:** Szczegóły sali konferencyjnej.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/rooms/00000000-0000-0000-0000-00000000000a" `
  -Method GET `
  -Headers @{ Authorization = "Basic $base64Admin" }

$response.Content
```

### Administrator — brak sali
**Oczekiwany kod odpowiedzi:** `404 Not Found`  
**Opis:** Sala konferencyjna nie została znaleziona.

```powershell
Invoke-WebRequest `
  -Uri "http://localhost:8080/rooms/bbbbbbbb-cccc-dddd-eeee-ffffffffffff" `
  -Method GET `
  -Headers @{ Authorization = "Basic $base64Admin" }
```

### Użytkownik — sukces
**Oczekiwany kod odpowiedzi:** `200 OK`  
**Opis:** Szczegóły sali konferencyjnej.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/rooms/00000000-0000-0000-0000-00000000000a" `
  -Method GET `
  -Headers @{ Authorization = "Basic $base64User" }

$response.Content
```

### Użytkownik — brak sali
**Oczekiwany kod odpowiedzi:** `404 Not Found`  
**Opis:** Sala konferencyjna nie została znaleziona.

```powershell
Invoke-WebRequest `
  -Uri "http://localhost:8080/rooms/bbbbbbbb-cccc-dddd-eeee-ffffffffffff" `
  -Method GET `
  -Headers @{ Authorization = "Basic $base64User" }
```

### Błędny użytkownik — sala istnieje
**Oczekiwany kod odpowiedzi:** `401 Unauthorized`  
**Opis:** Brak uwierzytelnienia lub niepoprawne dane logowania.

```powershell
try {
    Invoke-WebRequest `
      -Uri "http://localhost:8080/rooms/00000000-0000-0000-0000-00000000000a " `
      -Method GET `
      -Headers @{ Authorization = "Basic $base64Bad" }
} catch {
    $_.Exception.Response.StatusCode.value__
}
```

### Błędny użytkownik — sala nie istnieje
**Oczekiwany kod odpowiedzi:** `401 Unauthorized`  
**Opis:** Brak uwierzytelnienia lub niepoprawne dane logowania.

```powershell
try {
    Invoke-WebRequest `
      -Uri "http://localhost:8080/rooms/bbbbbbbb-cccc-dddd-eeee-ffffffffffff " `
      -Method GET `
      -Headers @{ Authorization = "Basic $base64Bad" }
} catch {
    $_.Exception.Response.StatusCode.value__
}
```

---

## Endpoint: `PUT /rooms/{id}`

Aktualizuje dane wybranej sali konferencyjnej. Endpoint dostępny tylko dla administratora.

### Administrator — sukces
**Oczekiwany kod odpowiedzi:** `200 OK`  
**Opis:** Sala konferencyjna została zaktualizowana.

```powershell
$body = @"
{
  "requestHeader": {
    "requestId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
    "sendDate": "2026-06-15T10:05:00Z"
  },
  "room": {
    "name": "Sala Konferencyjna A - Zmieniona",
    "capacity": 18,
    "location": "Budynek A",
    "floor": 2,
    "status": "AVAILABLE",
    "hasProjector": true,
    "hasWhiteboard": true,
    "hasDesktopComputer": true,
    "hasSpeakers": true,
    "hasVideoConferenceSystem": false
  }
}
"@

$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/rooms/00000000-0000-0000-0000-00000000000a" `
  -Method PUT `
  -Headers @{
    Authorization = "Basic $base64Admin"
    "Content-Type" = "application/json"
  } `
  -Body $body

$response.Content
```

### Administrator — brak sali
**Oczekiwany kod odpowiedzi:** `404 Not Found`  
**Opis:** Sala konferencyjna nie została znaleziona.

```powershell
$body = @"
{
  "requestHeader": {
    "requestId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
    "sendDate": "2026-06-15T10:05:00Z"
  },
  "room": {
    "name": "Sala Konferencyjna A - Zmieniona",
    "capacity": 18,
    "location": "Budynek A",
    "floor": 2,
    "status": "AVAILABLE",
    "hasProjector": true,
    "hasWhiteboard": true,
    "hasDesktopComputer": true,
    "hasSpeakers": true,
    "hasVideoConferenceSystem": false
  }
}
"@

Invoke-WebRequest `
  -Uri "http://localhost:8080/rooms/bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb" `
  -Method PUT `
  -Headers @{
    Authorization = "Basic $base64Admin"
    "Content-Type" = "application/json"
  } `
  -Body $body
```

### Administrator — konflikt danych
**Oczekiwany kod odpowiedzi:** `409 Conflict`  
**Opis:** Konflikt danych.

```powershell
$body = @"
{
  "requestHeader": {
    "requestId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
    "sendDate": "2026-06-15T10:05:00Z"
  },
  "room": {
    "name": "Sala Konferencyjna B",
    "capacity": 18,
    "location": "Budynek A, skrzydło wschodnie",
    "floor": 2,
    "status": "AVAILABLE",
    "hasProjector": true,
    "hasWhiteboard": true,
    "hasDesktopComputer": true,
    "hasSpeakers": true,
    "hasVideoConferenceSystem": false
  }
}
"@

Invoke-WebRequest `
  -Uri "http://localhost:8080/rooms/00000000-0000-0000-0000-00000000000a" `
  -Method PUT `
  -Headers @{
    Authorization = "Basic $base64Admin"
    "Content-Type" = "application/json"
  } `
  -Body $body
```

### Użytkownik
**Oczekiwany kod odpowiedzi:** `403 Forbidden`  
**Opis:** Brak uprawnień - operacja dostępna tylko dla ADMIN.

```powershell
$body = @"
{
  "requestHeader": {
    "requestId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
    "sendDate": "2026-06-15T10:05:00Z"
  },
  "room": {
    "name": "Sala Konferencyjna A - Zmieniona",
    "capacity": 18,
    "location": "Budynek A, skrzydło wschodnie",
    "floor": 2,
    "status": "AVAILABLE",
    "hasProjector": true,
    "hasWhiteboard": true,
    "hasDesktopComputer": true,
    "hasSpeakers": true,
    "hasVideoConferenceSystem": false
  }
}
"@

try {
  Invoke-WebRequest `
    -Uri "http://localhost:8080/rooms/00000000-0000-0000-0000-00000000000a" `
    -Method PUT `
    -Headers @{
      Authorization = "Basic $base64User"
      "Content-Type" = "application/json"
    } `
    -Body $body
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

### Błędny użytkownik
**Oczekiwany kod odpowiedzi:** `401 Unauthorized`  
**Opis:** Brak uwierzytelnienia lub niepoprawne dane logowania.

```powershell
$body = @"
{
  "requestHeader": {
    "requestId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
    "sendDate": "2026-06-15T10:05:00Z"
  },
  "room": {
    "name": "Sala Konferencyjna A - Zmieniona",
    "capacity": 18,
    "location": "Budynek A, skrzydło wschodnie",
    "floor": 2,
    "status": "AVAILABLE",
    "hasProjector": true,
    "hasWhiteboard": true,
    "hasDesktopComputer": true,
    "hasSpeakers": true,
    "hasVideoConferenceSystem": false
  }
}
"@

try {
  Invoke-WebRequest `
    -Uri "http://localhost:8080/rooms/00000000-0000-0000-0000-00000000000a" `
    -Method PUT `
    -Headers @{
      Authorization = "Basic $base64Bad"
      "Content-Type" = "application/json"
    } `
    -Body $body
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

---

## Endpoint: `PATCH /rooms/{id}/status`

Zmienia status sali konferencyjnej. Endpoint dostępny tylko dla administratora.

### Administrator — sukces
**Oczekiwany kod odpowiedzi:** `200 OK`  
**Opis:** Status sali konferencyjnej został zmieniony.

```powershell
$body = @"
{
  "requestHeader": {
    "requestId": "cccccccc-cccc-cccc-cccc-cccccccccccc",
    "sendDate": "2026-06-15T10:10:00Z"
  },
  "status": "UNDER_MAINTENANCE"
}
"@

$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/rooms/00000000-0000-0000-0000-00000000000a/status" `
  -Method PATCH `
  -Headers @{
    Authorization = "Basic $base64Admin"
    "Content-Type" = "application/json"
  } `
  -Body $body

$response.Content
```

### Administrator — przywrócenie statusu do dalszych testów

```powershell
$body = @"
{
  "requestHeader": {
    "requestId": "cccccccc-cccc-cccc-cccc-cccccccccccc",
    "sendDate": "2026-06-15T10:10:00Z"
  },
  "status": "AVAILABLE"
}
"@

$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/rooms/00000000-0000-0000-0000-00000000000a/status" `
  -Method PATCH `
  -Headers @{
    Authorization = "Basic $base64Admin"
    "Content-Type" = "application/json"
  } `
  -Body $body

$response.Content
```

### Administrator — błędne dane wejściowe
**Oczekiwany kod odpowiedzi:** `400 Bad Request`  
**Opis:** Niepoprawne dane wejściowe.

```powershell
$body = @"
{
  "requestHeader": {
    "requestId": "cccccccc-cccc-cccc-cccc-cccccccccccc",
    "sendDate": "2026-06-15T10:10:00Z"
  },
  "status": "UNDERRRR_MAINTCNE"
}
"@

$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/rooms/00000000-0000-0000-0000-00000000000a/status" `
  -Method PATCH `
  -Headers @{
    Authorization = "Basic $base64Admin"
    "Content-Type" = "application/json"
  } `
  -Body $body
```

### Administrator — brak sali
**Oczekiwany kod odpowiedzi:** `404 Not Found`  
**Opis:** Sala konferencyjna nie została znaleziona.

```powershell
$body = @"
{
  "requestHeader": {
    "requestId": "cccccccc-cccc-cccc-cccc-cccccccccccc",
    "sendDate": "2026-06-15T10:10:00Z"
  },
  "status": "UNDER_MAINTENANCE"
}
"@

$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/rooms/cccccccc-cccc-cccc-cccc-cccccccccccc/status" `
  -Method PATCH `
  -Headers @{
    Authorization = "Basic $base64Admin"
    "Content-Type" = "application/json"
  } `
  -Body $body
```

### Użytkownik
**Oczekiwany kod odpowiedzi:** `403 Forbidden`  
**Opis:** Brak uprawnień - operacja dostępna tylko dla ADMIN.

```powershell
$body = @"
{
  "requestHeader": {
    "requestId": "cccccccc-cccc-cccc-cccc-cccccccccccc",
    "sendDate": "2026-06-15T10:10:00Z"
  },
  "status": "UNDER_MAINTENANCE"
}
"@

try {
  Invoke-WebRequest `
    -Uri "http://localhost:8080/rooms/00000000-0000-0000-0000-00000000000a/status" `
    -Method PATCH `
    -Headers @{
      Authorization = "Basic $base64User"
      "Content-Type" = "application/json"
    } `
    -Body $body
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

### Błędny użytkownik
**Oczekiwany kod odpowiedzi:** `401 Unauthorized`  
**Opis:** Brak uwierzytelnienia lub niepoprawne dane logowania.

```powershell
$body = @"
{
  "requestHeader": {
    "requestId": "cccccccc-cccc-cccc-cccc-cccccccccccc",
    "sendDate": "2026-06-15T10:10:00Z"
  },
  "status": "UNDER_MAINTENANCE"
}
"@

try {
  Invoke-WebRequest `
    -Uri "http://localhost:8080/rooms/00000000-0000-0000-0000-00000000000a/status" `
    -Method PATCH `
    -Headers @{
      Authorization = "Basic $base64Bad"
      "Content-Type" = "application/json"
    } `
    -Body $body
} catch {
  $_.Exception.Response.StatusCode.value__
}
```
# Reservations

## Endpoint: `GET /reservations`

Zwraca listę wszystkich rezerwacji. Endpoint dostępny tylko dla administratora.

### Administrator
**Oczekiwany kod odpowiedzi:** `200 OK`  
**Opis:** Lista wszystkich rezerwacji.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/reservations" `
  -Method GET `
  -Headers @{ Authorization = "Basic $base64Admin" }

$response.Content
```

### Użytkownik
**Oczekiwany kod odpowiedzi:** `403 Forbidden`  
**Opis:** Brak uprawnień - operacja dostępna tylko dla ADMIN.

```powershell
try {
    Invoke-WebRequest `
      -Uri "http://localhost:8080/reservations" `
      -Method GET `
      -Headers @{ Authorization = "Basic $base64User" }
} catch {
    $_.Exception.Response.StatusCode.value__
}
```

### Błędny użytkownik
**Oczekiwany kod odpowiedzi:** `401 Unauthorized`  
**Opis:** Brak uwierzytelnienia lub niepoprawne dane logowania.

```powershell
try {
    Invoke-WebRequest `
      -Uri "http://localhost:8080/reservations" `
      -Method GET `
      -Headers @{ Authorization = "Basic $base64Bad" }
} catch {
    $_.Exception.Response.StatusCode.value__
}
```

---

## Endpoint: `POST /reservations`

Tworzy nową rezerwację sali konferencyjnej.

### Funkcja do liczenia HMAC

```powershell
function Get-HmacSignature {
    param(
        [string]$RequestId,
        [string]$SendDate,
        [string]$RoomId,
        [string]$MeetingTitle,
        [string]$Description,
        [string]$StartDateTime,
        [string]$EndDateTime
    )

    $secret = "super-secret-hmac-key"
    $canonical = $RequestId + "|" + $SendDate + "|" + $RoomId + "|" + $MeetingTitle + "|" + $Description + "|" + $StartDateTime + "|" + $EndDateTime

    $hmac = New-Object System.Security.Cryptography.HMACSHA256
    $hmac.Key = [System.Text.Encoding]::UTF8.GetBytes($secret)
    $hashBytes = $hmac.ComputeHash([System.Text.Encoding]::UTF8.GetBytes($canonical))
    $signature = -join ($hashBytes | ForEach-Object { $_.ToString("x2") })

    return $signature
}
```

### Funkcja do wywołania requestu

```powershell
function Invoke-ReservationPost {
    param(
        [string]$AuthBase64,
        [string]$RequestId,
        [string]$SendDate,
        [string]$RoomId,
        [string]$MeetingTitle,
        [string]$Description,
        [string]$StartDateTime,
        [string]$EndDateTime,
        [string]$Signature
    )

    $body = @"
{
  "requestHeader": {
    "requestId": "$RequestId",
    "sendDate": "$SendDate"
  },
  "reservation": {
    "roomId": "$RoomId",
    "meetingTitle": "$MeetingTitle",
    "description": "$Description",
    "startDateTime": "$StartDateTime",
    "endDateTime": "$EndDateTime"
  }
}
"@

    try {
        $response = Invoke-WebRequest `
          -Uri "http://localhost:8080/reservations" `
          -Method POST `
          -Headers @{
            Authorization = "Basic $AuthBase64"
            "Content-Type" = "application/json"
            "X-HMAC-SIGNATURE" = $Signature
          } `
          -Body $body

        $response.StatusCode
        $response.Content
    }
    catch {
        $_.Exception.Response.StatusCode.value__
        $_.ErrorDetails.Message
    }
}
```

### Sukces
**Oczekiwany kod odpowiedzi:** `201 Created`  
**Opis:** Rezerwacja została utworzona + wysłany mail potwierdzający.

```powershell
$requestId = "11111111-1111-1111-1111-111111111111"
$sendDate = "2026-06-16T10:00Z"
$roomId = "00000000-0000-0000-0000-00000000000c"
$meetingTitle = "Nowe spotkanie projektowe"
$description = "Omowienie harmonogramu wdrozenia"
$startDateTime = "2026-07-16T10:00Z"
$endDateTime = "2026-07-16T11:00Z"

$signature = Get-HmacSignature `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime

Invoke-ReservationPost `
  -AuthBase64 $base64User2 `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime `
  -Signature $signature
```

### Błąd 400 — niepoprawny zakres czasu
**Kod błędu biznesowego:** `INVALID_TIME_RANGE`

```powershell
$requestId = "22222222-2222-2222-2222-222222222222"
$sendDate = "2026-06-16T10:00Z"
$roomId = "00000000-0000-0000-0000-00000000000c"
$meetingTitle = "Zly zakres czasu"
$description = "Test invalid time range"
$startDateTime = "2026-07-16T12:00Z"
$endDateTime = "2026-07-16T11:00Z"

$signature = Get-HmacSignature `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime

Invoke-ReservationPost `
  -AuthBase64 $base64User `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime `
  -Signature $signature
```

### Błąd 400 — rezerwacja poza godzinami pracy
**Kod błędu biznesowego:** `RESERVATION_OUTSIDE_WORKING_HOURS`

```powershell
$requestId = "33333333-3333-3333-3333-333333333333"
$sendDate = "2026-06-16T10:00Z"
$roomId = "00000000-0000-0000-0000-00000000000c"
$meetingTitle = "Poza godzinami pracy"
$description = "Test outside working hours"
$startDateTime = "2026-07-16T19:00Z"
$endDateTime = "2026-07-16T20:00Z"

$signature = Get-HmacSignature `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime

Invoke-ReservationPost `
  -AuthBase64 $base64User `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime `
  -Signature $signature
```

### Błąd 400 — nieprawidłowy czas trwania
**Kod błędu biznesowego:** `INVALID_RESERVATION_DURATION`

```powershell
$requestId = "44444444-4444-4444-4444-444444444444"
$sendDate = "2026-06-16T10:00Z"
$roomId = "00000000-0000-0000-0000-00000000000c"
$meetingTitle = "Zly czas trwania"
$description = "Test invalid duration"
$startDateTime = "2026-07-16T10:05Z"
$endDateTime = "2026-07-16T10:35Z"

$signature = Get-HmacSignature `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime

Invoke-ReservationPost `
  -AuthBase64 $base64User `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime `
  -Signature $signature
```

### Błąd 400 — niepoprawne dane wejściowe
**Kod błędu biznesowego:** `INVALID_REQUEST`

```powershell
$requestId = "55555555-5555-5555-5555-555555555555"
$sendDate = "2026-06-16T10:00Z"
$roomId = "00000000-0000-0000-0000-00000000000c"
$meetingTitle = ""
$description = "Test invalid request"
$startDateTime = "2026-07-16T10:00Z"
$endDateTime = "2026-07-16T11:00Z"

$signature = Get-HmacSignature `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime

$body = @"
{
  "requestHeader": {
    "requestId": "$requestId",
    "sendDate": "$sendDate"
  },
  "reservation": {
    "roomId": "$roomId",
    "meetingTitle": "$meetingTitle",
    "description": "$description",
    "startDateTime": "$startDateTime",
    "endDateTime": "$endDateTime"
  }
}
"@

try {
    Invoke-WebRequest `
      -Uri "http://localhost:8080/reservations" `
      -Method POST `
      -Headers @{
        Authorization = "Basic $base64User"
        "Content-Type" = "application/json"
        "X-HMAC-SIGNATURE" = $signature
      } `
      -Body $body
} catch {
    $_.Exception.Response.StatusCode.value__
    $_.ErrorDetails.Message
}
```

### Błąd 401
**Oczekiwany kod odpowiedzi:** `401 Unauthorized`  
**Opis:** Brak uwierzytelnienia lub niepoprawne dane logowania.

```powershell
$requestId = "66666666-6666-6666-6666-666666666666"
$sendDate = "2026-06-16T10:00Z"
$roomId = "00000000-0000-0000-0000-00000000000c"
$meetingTitle = "Test 401"
$description = "Test unauthorized"
$startDateTime = "2026-07-16T10:00Z"
$endDateTime = "2026-07-16T11:00Z"

$signature = Get-HmacSignature `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime

Invoke-ReservationPost `
  -AuthBase64 $base64Bad `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime `
  -Signature $signature
```

### Błąd 403
**Oczekiwany kod odpowiedzi:** `403 Forbidden`  
**Opis:** Brak uprawnień, np. niepoprawny podpis HMAC.

```powershell
$requestId = "77777777-7777-7777-7777-777777777777"
$sendDate = "2026-06-16T10:00Z"
$roomId = "00000000-0000-0000-0000-00000000000c"
$meetingTitle = "Zly HMAC"
$description = "Test invalid hmac"
$startDateTime = "2026-07-16T10:00Z"
$endDateTime = "2026-07-16T11:00Z"

Invoke-ReservationPost `
  -AuthBase64 $base64User `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime `
  -Signature "zly-podpis"
```

### Błąd 404
**Oczekiwany kod odpowiedzi:** `ROOM_NOT_FOUND`  
**Opis:** Sala nie została znaleziona.

```powershell
$requestId = "88888888-8888-8888-8888-888888888888"
$sendDate = "2026-06-16T10:00Z"
$roomId = "99999999-9999-9999-9999-999999999999"
$meetingTitle = "Sala nie istnieje"
$description = "Test room not found"
$startDateTime = "2026-07-16T10:00Z"
$endDateTime = "2026-07-16T11:00Z"

$signature = Get-HmacSignature `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime

Invoke-ReservationPost `
  -AuthBase64 $base64User `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime `
  -Signature $signature
```

### Błąd 409 — sala jest już zajęta
**Kod błędu biznesowego:** `ROOM_ALREADY_BOOKED`

```powershell
$requestId = "99999999-9999-9999-9999-999999999999"
$sendDate = "2026-06-16T09:00Z"
$roomId = "00000000-0000-0000-0000-00000000000a"
$meetingTitle = "Konflikt sali"
$description = "Test room already booked"
$startDateTime = "2026-07-16T10:00Z"
$endDateTime = "2026-07-16T12:00Z"

$signature = Get-HmacSignature `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime

Invoke-ReservationPost `
  -AuthBase64 $base64Admin `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime `
  -Signature $signature
```

### Błąd 409 — sala jest niedostępna
**Kod błędu biznesowego:** `ROOM_NOT_AVAILABLE`

```powershell
$requestId = "aaaaaaaa-1111-1111-1111-111111111111"
$sendDate = "2026-06-16T10:00Z"
$roomId = "00000000-0000-0000-0000-00000000000b"
$meetingTitle = "Sala niedostepna"
$description = "Test room not available"
$startDateTime = "2026-07-16T10:00Z"
$endDateTime = "2026-07-16T11:00Z"

$signature = Get-HmacSignature `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime

Invoke-ReservationPost `
  -AuthBase64 $base64User `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime `
  -Signature $signature
```

### Błąd 409 — użytkownik ma już inną rezerwację
**Kod błędu biznesowego:** `USER_ALREADY_HAS_RESERVATION_AT_THIS_TIME`

```powershell
$requestId = "bbbbbbbb-2222-2222-2222-222222222222"
$sendDate = "2026-06-16T09:00Z"
$roomId = "00000000-0000-0000-0000-00000000000c"
$meetingTitle = "Konflikt uzytkownika"
$description = "Test user already has reservation"
$startDateTime = "2026-07-16T10:00Z"
$endDateTime = "2026-07-16T11:00Z"

$signature = Get-HmacSignature `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime

Invoke-ReservationPost `
  -AuthBase64 $base64User `
  -RequestId $requestId `
  -SendDate $sendDate `
  -RoomId $roomId `
  -MeetingTitle $meetingTitle `
  -Description $description `
  -StartDateTime $startDateTime `
  -EndDateTime $endDateTime `
  -Signature $signature
```
---

## Endpoint: `GET /reservations/{id}`

Zwraca szczegóły jednej rezerwacji.

### Administrator — sukces
**Oczekiwany kod odpowiedzi:** `200 OK`  
**Opis:** Szczegóły rezerwacji.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/reservations/00000000-0000-0000-0000-000000000001" `
  -Method GET `
  -Headers @{ Authorization = "Basic $base64Admin" }

$response.Content
```

### Administrator — brak rezerwacji
**Oczekiwany kod odpowiedzi:** `404 Not Found`  
**Opis:** Rezerwacja nie została znaleziona.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/reservations/00000000-0000-0000-0000-000000000011" `
  -Method GET `
  -Headers @{ Authorization = "Basic $base64Admin" }

$response.Content
```

### Użytkownik — sukces
**Oczekiwany kod odpowiedzi:** `200 OK`  
**Opis:** Szczegóły rezerwacji.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/reservations/00000000-0000-0000-0000-000000000001" `
  -Method GET `
  -Headers @{ Authorization = "Basic $base64User" }

$response.Content
```

### Użytkownik — brak uprawnień
**Oczekiwany kod odpowiedzi:** `403 Forbidden`  
**Opis:** Brak uprawnień do przeglądania wskazanej rezerwacji.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/reservations/00000000-0000-0000-0000-000000000002" `
  -Method GET `
  -Headers @{ Authorization = "Basic $base64User" }

$response.Content
```

### Użytkownik — brak rezerwacji
**Oczekiwany kod odpowiedzi:** `404 Not Found`  
**Opis:** Rezerwacja nie została znaleziona.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/reservations/00000000-0000-0000-0000-000000000011" `
  -Method GET `
  -Headers @{ Authorization = "Basic $base64User" }

$response.Content
```

### Błędny użytkownik
**Oczekiwany kod odpowiedzi:** `401 Unauthorized`  
**Opis:** Brak uwierzytelnienia lub niepoprawne dane logowania.

```powershell
try {
  Invoke-WebRequest `
    -Uri "http://localhost:8080/reservations/00000000-0000-0000-0000-000000000001" `
    -Method GET `
    -Headers @{ Authorization = "Basic $base64Bad" }
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

---

## Endpoint: `POST /reservations/{id}/cancel`

Anuluje wskazaną rezerwację.

### Administrator — sukces
**Oczekiwany kod odpowiedzi:** `200 OK`  
**Opis:** Rezerwacja została anulowana.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/reservations/00000000-0000-0000-0000-000000000002/cancel" `
  -Method POST `
  -Headers @{ Authorization = "Basic $base64Admin" }

$response.Content
```

### Administrator — brak rezerwacji
**Oczekiwany kod odpowiedzi:** `404 Not Found`  
**Opis:** Rezerwacja nie została znaleziona.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/reservations/00000000-0000-0000-0000-000000000011/cancel" `
  -Method POST `
  -Headers @{ Authorization = "Basic $base64Admin" }

$response.Content
```

### Administrator — rezerwacja już się rozpoczęła lub zakończyła
**Oczekiwany kod odpowiedzi:** `409 Conflict`  
**Opis:** Konflikt biznesowy – rezerwacja już się rozpoczęła lub zakończyła.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/reservations/00000000-0000-0000-0000-000000000003/cancel" `
  -Method POST `
  -Headers @{ Authorization = "Basic $base64Admin" }

$response.Content
```

### Administrator — rezerwacja już miała status cancelled
**Oczekiwany kod odpowiedzi:** `409 Conflict`  
**Opis:** Konflikt biznesowy – rezerwacja już miała status cancelled.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/reservations/00000000-0000-0000-0000-000000000002/cancel" `
  -Method POST `
  -Headers @{ Authorization = "Basic $base64Admin" }

$response.Content

$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/reservations/00000000-0000-0000-0000-000000000002/cancel" `
  -Method POST `
  -Headers @{ Authorization = "Basic $base64Admin" }

$response.Content
```

### Użytkownik — sukces
**Oczekiwany kod odpowiedzi:** `200 OK`  
**Opis:** Rezerwacja została anulowana.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/reservations/00000000-0000-0000-0000-000000000001/cancel" `
  -Method POST `
  -Headers @{ Authorization = "Basic $base64User" }

$response.Content
```

### Użytkownik — brak uprawnień
**Oczekiwany kod odpowiedzi:** `403 Forbidden`  
**Opis:** Brak uprawnień do anulowania wskazanej rezerwacji.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/reservations/00000000-0000-0000-0000-000000000002/cancel" `
  -Method POST `
  -Headers @{ Authorization = "Basic $base64User" }

$response.Content
```

### Użytkownik — brak rezerwacji
**Oczekiwany kod odpowiedzi:** `404 Not Found`  
**Opis:** Rezerwacja nie została znaleziona.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/reservations/00000000-0000-0000-0000-000000000011/cancel" `
  -Method POST `
  -Headers @{ Authorization = "Basic $base64User" }

$response.Content
```

### Użytkownik — rezerwacja już się rozpoczęła lub zakończyła
**Oczekiwany kod odpowiedzi:** `409 Conflict`  
**Opis:** Konflikt biznesowy - rezerwacja już się rozpoczęła lub zakończyła.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/reservations/00000000-0000-0000-0000-000000000003/cancel" `
  -Method POST `
  -Headers @{ Authorization = "Basic $base64User" }

$response.Content
```

### Użytkownik — rezerwacja już miała status cancelled
**Oczekiwany kod odpowiedzi:** `409 Conflict`  
**Opis:** Konflikt biznesowy – rezerwacja już miała status cancelled.

```powershell
$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/reservations/00000000-0000-0000-0000-000000000001/cancel" `
  -Method POST `
  -Headers @{ Authorization = "Basic $base64User" }

$response.Content

$response = Invoke-WebRequest `
  -Uri "http://localhost:8080/reservations/00000000-0000-0000-0000-000000000001/cancel" `
  -Method POST `
  -Headers @{ Authorization = "Basic $base64User" }

$response.Content
```

### Błędny użytkownik
**Oczekiwany kod odpowiedzi:** `401 Unauthorized`  
**Opis:** Brak uwierzytelnienia lub niepoprawne dane logowania.

```powershell
try {
  Invoke-WebRequest `
    -Uri "http://localhost:8080/reservations/00000000-0000-0000-0000-000000000001/cancel" `
    -Method POST `
    -Headers @{ Authorization = "Basic $base64Bad" }
} catch {
  $_.Exception.Response.StatusCode.value__
}
```
