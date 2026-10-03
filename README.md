# 🅿️ Parking Lot Management System — SDE-1 LLD (Java)

A production-quality **Low-Level Design** implementation of a Parking Lot Management System in Java, demonstrating SOLID principles, multiple GoF design patterns, and clean modular architecture.

---

## 📁 Project Structure

```
src/main/java/com/parkinglot/
├── enums/
│   ├── VehicleType.java         # BIKE | CAR | TRUCK
│   ├── SpotType.java            # BIKE | CAR | TRUCK
│   ├── SpotStatus.java          # AVAILABLE | OCCUPIED | MAINTENANCE
│   └── PaymentMethod.java       # CASH | CREDIT_CARD | UPI
│
├── models/
│   ├── vehicle/
│   │   ├── Vehicle.java         # Abstract base (identity + type)
│   │   ├── Bike.java
│   │   ├── Car.java
│   │   └── Truck.java
│   ├── spot/
│   │   ├── ParkingSpot.java     # Abstract base (Template Method)
│   │   ├── BikeSpot.java        # canFit: BIKE only
│   │   ├── CarSpot.java         # canFit: BIKE + CAR
│   │   └── TruckSpot.java       # canFit: ALL
│   ├── ParkingFloor.java        # Manages spots on one floor
│   ├── ParkingLot.java          # Singleton aggregate root
│   ├── Ticket.java              # Entry receipt (immutable-style)
│   └── Payment.java             # Payment receipt
│
├── strategy/
│   ├── pricing/
│   │   ├── PricingStrategy.java          # Interface
│   │   ├── HourlyPricingStrategy.java    # ₹10/20/40 per hour
│   │   └── PeakHourPricingStrategy.java  # 1.5× during 08-10 & 17-20
│   └── allocation/
│       ├── SpotAllocationStrategy.java   # Interface
│       ├── FirstAvailableStrategy.java   # Floor-by-floor scan
│       └── NearestEntranceStrategy.java  # Minimise walk distance
│
├── factory/
│   ├── VehicleFactory.java              # Creates Vehicle subclasses
│   └── PaymentProcessorFactory.java     # Creates PaymentProcessor impls
│
├── payment/
│   ├── PaymentProcessor.java            # Interface
│   ├── CashPaymentProcessor.java
│   ├── CardPaymentProcessor.java
│   └── UPIPaymentProcessor.java
│
├── service/
│   ├── ParkingService.java     # Facade: check-in / check-out
│   ├── TicketService.java      # Ticket lifecycle management
│   └── PaymentService.java     # Fee calculation + payment
│
├── display/
│   └── DisplayBoard.java       # Console status display
│
├── exception/
│   ├── ParkingLotException.java     # Base checked exception
│   ├── ParkingFullException.java
│   ├── InvalidTicketException.java
│   └── PaymentException.java
│
└── Main.java                   # 8-scenario demo driver
```

---

## 🏗️ Design Patterns

| Pattern | Where Applied |
|---|---|
| **Strategy** | `PricingStrategy` (Hourly / PeakHour), `SpotAllocationStrategy` (FirstAvailable / NearestEntrance) |
| **Factory** | `VehicleFactory`, `PaymentProcessorFactory` |
| **Singleton** | `ParkingLot` — thread-safe double-checked locking |
| **Template Method** | `ParkingSpot.canFit()` — abstract hook, shared park/free logic in base |
| **Facade** | `ParkingService` — hides allocation + ticketing + payment complexity |

---

## 🧱 SOLID Principles

| Principle | Implementation |
|---|---|
| **SRP** | `Ticket` stores entry data only; `PricingStrategy` computes fees only; `DisplayBoard` renders only |
| **OCP** | Add `ElectricVehicle` or `DailyPricingStrategy` without touching existing classes |
| **LSP** | `Bike/Car/Truck` fully substitutable for `Vehicle`; all spots for `ParkingSpot` |
| **ISP** | `PaymentProcessor`, `PricingStrategy`, `SpotAllocationStrategy` — each a focused, minimal interface |
| **DIP** | `ParkingService` depends on `SpotAllocationStrategy` + `PricingStrategy` abstractions, injected at construction |

---

## ⚙️ Vehicle ↔ Spot Compatibility Matrix

| Vehicle \ Spot | BikeSpot | CarSpot | TruckSpot |
|---|:---:|:---:|:---:|
| **Bike**  | ✅ | ✅ | ✅ |
| **Car**   | ❌ | ✅ | ✅ |
| **Truck** | ❌ | ❌ | ✅ |

---

## 💰 Pricing Strategies

### HourlyPricingStrategy
| Vehicle | Rate |
|---|---|
| Bike  | ₹10 / hour |
| Car   | ₹20 / hour |
| Truck | ₹40 / hour |
- Minimum charge: 1 hour
- Fractional hours rounded **up** (ceiling)

### PeakHourPricingStrategy
- All base rates × **1.5** during 08:00–10:00 and 17:00–20:00
- Off-peak: standard hourly rates
- Composed over `HourlyPricingStrategy` (not inherited)

---

## 🚗 Key Flows

### Check-In
```
Main → ParkingService.checkIn(vehicle)
         → SpotAllocationStrategy.allocate(lot, vehicle)   // finds best spot
         → ParkingSpot.park(vehicle)                       // marks OCCUPIED
         → TicketService.generateTicket(vehicle, spot)     // issues receipt
         ← Ticket
```

### Check-Out
```
Main → ParkingService.checkOut(ticketId, paymentMethod)
         → TicketService.getActiveTicket(ticketId)         // validates
         → PaymentService.processPayment(ticket, method)
             → PricingStrategy.calculateFee(ticket, now)   // compute fee
             → PaymentProcessorFactory.getProcessor(method) // get processor
             → PaymentProcessor.processPayment(amount)     // charge
         → ParkingSpot.free()                              // marks AVAILABLE
         → TicketService.closeTicket(ticketId)             // marks paid
         ← Payment receipt
```

---

## 🧪 Test Scenarios (Main.java)

| # | Scenario | Validates |
|---|---|---|
| 1 | Multi-vehicle check-ins (Bike, Car, Truck) | Allocation, ticket generation |
| 2 | Fee estimation before payment | Preview without charging |
| 3 | Check-outs with Cash / Card / UPI | All payment methods |
| 4 | Duplicate checkout attempt | `InvalidTicketException` handling |
| 5 | Overflow — all bike spots full | Bikes spill into CarSpots (NearestEntrance) |
| 6 | Strategy swap (PeakHour + FirstAvailable) | Runtime strategy injection |
| 7 | Truck check-ins, UPI payment | Peak-hour pricing path |
| 8 | Unknown ticket ID | `InvalidTicketException` from fake ID |

---

## 🚀 Build & Run

```bash
# 1. Collect all sources
Get-ChildItem -Recurse -Filter "*.java" src\main\java | Select-Object -ExpandProperty FullName | Out-File sources.txt

# 2. Compile
$files = Get-Content sources.txt
javac -d out -encoding UTF-8 $files

# 3. Run
java -cp out com.parkinglot.Main
```

---

## 🔌 Extending the System

### Add a new vehicle type (e.g., `ElectricBike`)
1. Add `ELECTRIC_BIKE` to `VehicleType` enum
2. Create `ElectricBike extends Vehicle`
3. Add a case to `VehicleFactory.create()`
4. Optionally create an `ElectricBikeSpot` or reuse `BikeSpot`

### Add a new pricing model (e.g., `DailyFlatRate`)
1. Create `DailyFlatRateStrategy implements PricingStrategy`
2. Inject into `PaymentService` — **zero existing code changes**

### Add a new payment method (e.g., `Wallet`)
1. Add `WALLET` to `PaymentMethod` enum
2. Create `WalletPaymentProcessor implements PaymentProcessor`
3. Add a case to `PaymentProcessorFactory.getProcessor()`
