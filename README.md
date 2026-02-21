# 📏 Quantity Measurement Application

A Java-based application that demonstrates measurement equality comparison using object-oriented principles.

This project evolves through three use cases:

- **UC1** – Feet Measurement Equality  
- **UC2** – Feet and Inches Equality  
- **UC3** – Generic Length Class (DRY Principle Applied)

---

# 🧩 UC1 – Feet Measurement Equality

## 📌 Overview

UC1 implements equality comparison between two numerical values measured in **Feet**.

## 🎯 Objective

- Compare two feet measurements
- Implement proper `equals()` override
- Handle null and type safety
- Follow OOP best practices

## 🏗 Implementation

Class Used:
- `Feet`

Key Features:
- Immutable value (`private final double value`)
- Proper `equals()` override
- Uses `Double.compare()` for floating-point comparison
- Null-safe and type-safe comparison

## 🔍 Example

```java
Feet f1 = new Feet(1.0);
Feet f2 = new Feet(1.0);

System.out.println(f1.equals(f2)); // true
