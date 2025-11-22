
package project.oop;

public class Assignment {
    private String id; 
    private String driverId; 
    private String busId; 
    private String routeId; 
    private String assignmentDate; 
    private String shift; 

    public Assignment(String id, String driverId, String busId, String routeId, String assignmentDate, String shift) {
        this.id = id;
        this.driverId = driverId;
        this.busId = busId;
        this.routeId = routeId;
        this.assignmentDate = assignmentDate;
        this.shift = shift;
    }

    public String getId() { 
        return id; 
    }
    public void setId(String id) { 
        this.id = id; 
    }

    public String getDriverId() { 
        return driverId; 
    }
    public void setDriverId(String driverId) { 
        this.driverId = driverId; 
    }

    public String getBusId() { 
        return busId; 
    }
    public void setBusId(String busId) { 
        this.busId = busId; 
    }

    public String getRouteId() { 
        return routeId; 
    }
    public void setRouteId(String routeId) { 
        this.routeId = routeId; 
    }

    public String getAssignmentDate() { 
        return assignmentDate; 
    }
    public void setAssignmentDate(String assignmentDate) { 
        this.assignmentDate = assignmentDate; 
    }

    public String getShift() { 
        return shift; 
    }
    public void setShift(String shift) { 
        this.shift = shift; 
    }


    public void displayDetail() {
        System.out.println("----Assignment----");
        System.out.println("ID Assignment: " + id);
        System.out.println("Driver (ID): " + driverId);
        System.out.println("Bus (ID): " + busId);
        System.out.println("Route (ID): " + routeId);
        System.out.println("Date: " + assignmentDate);
        System.out.println("Shift: " + shift);
        System.out.println("------------------------------------");
    }
}
