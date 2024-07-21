import java.util.Scanner;

public class Naiad {
  private double battery = 309.6; // battery at full capacity
  private double currentCapacity; // current battery capacity

  public Naiad(double initialCapacity) {
    this.currentCapacity = initialCapacity;
  }

    // watt hours produced by solar panels depending on where the rover is in the mission
  public static double model(double distance) {
    if (distance <= 1.42) {
      return 411 * 1.296 * (distance / 0.54);
    } else if (distance > 1.42 && distance <= 4.875) {
      return 1.296 * (distance / 0.54) * 411 * (1 - ((distance - 1.42) / 3.455));
    } else {
      return 0;
    }
  }

    // time in hours to fully charge the rover battery
  public double chargeTime(double distance) {
    double powerProduced = model(distance);
    if (powerProduced == 0) {
      return Double.POSITIVE_INFINITY; // if no power is produced, it will never fully charge
    }
    return currentCapacity / powerProduced;
  }

    // battery decrease over time with a full power draw of 278.7 watts (no science instruments)
  public void simulateDischarge(double distance) {
    double hours = distance / 0.54;	
    double powerDraw = 278.7; // full power draw in watts
    double energyConsumed = powerDraw * hours; // energy consumed in watt-hours
    currentCapacity -= energyConsumed;
    if (currentCapacity < 0) {
      currentCapacity = 0;
    }
  }

    // get current battery capacity
  public double getCurrentCapacity() {
    return currentCapacity;
  }

    // power per meter depending on where the rover is
  public double powerPerMeter(double distance) {
    double powerProduced = model(distance);
    if (distance == 0) {
      return 0; // no division by zero
    }
    //return powerProduced / (distance * 1000); // convert to meters
    return (278.7 * (distance / 0.54)) / (distance * 1000);

  }

    // total power consumption for the entire mission
  public double totalPowerConsumption() {
    double segment1Distance = 1.42;
    double segment2Distance = 4.875 - 1.42;
    double pI = 411 * 1.296 * (segment1Distance / 0.54);

        // power consumption for segment 1 (0 to 1.42 km)
    double powerSegment1 = pI * segment1Distance;

        // power consumption for segment 2 (1.42 km to 4.875 km)
        // This requires integration of the linear decrease from P0 to 0
    double powerSegment2 = (pI * segment2Distance) / 2;

        // total power consumption
    double totalPower = powerSegment1 + powerSegment2;
    return totalPower;
  }

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Enter the initial capacity of the rover's battery in watt hours (309.6 for full): ");
    double initialCap = scanner.nextDouble();

    Naiad rover = new Naiad(initialCap);

    System.out.print("Enter the distance the rover has traveled (km): ");
    double distance = scanner.nextDouble();
    System.out.println("Power produced at distance " + distance + " km: " + model(distance) + " W");

    double chargeTime = rover.chargeTime(distance);
    System.out.println("Time to fully charge: " + chargeTime + " hours");

    double dischargeHours = distance / 0.54;
    rover.simulateDischarge(dischargeHours);
    System.out.println("Battery capacity after " + dischargeHours + " hours of discharge: " + rover.getCurrentCapacity() + " Wh");

    double powerPerMeter = rover.powerPerMeter(distance);
    System.out.println("Power per meter at distance " + distance + " km: " + powerPerMeter + " W/m");

    double totalPowerConsumption = rover.totalPowerConsumption();
    System.out.println("Total power consumption for the entire mission: " + totalPowerConsumption + " Wh");

    scanner.close();
    }
}
