
package project.oop;

public class Route {
    private String id;
    private String name;
    private String startPoint; 
    private String endPoint; 
    private double distance;

    // Constructor đầy đủ
    public Route(String id, String name, String startPoint, String endPoint, double distance) {
        this.id = id;
        this.name = name;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.distance = distance;
    }


    public String getId() { 
        return id; 
    }
    public void setId(String id) { 
        this.id = id; 
    }

    public String getName() { 
        return name; 
    }
    public void setName(String name) { 
        this.name = name; 
    }

    public String getStartPoint() { 
        return startPoint; 
    }
    public void setStartPoint(String startPoint) { 
        this.startPoint = startPoint; 
    }

    public String getEndPoint() { 
        return endPoint; 
    }
    public void setEndPoint(String endPoint) { 
        this.endPoint = endPoint; 
    }

    public double getDistance() { 
        return distance; 
    }
    public void setDistance(double distance) { 
        this.distance = distance; 
    }

    public void displayDetail() {
        System.out.println("----Detail of Route----");
        System.out.println("ID: " + id);
        System.out.println("Route's name: " + name);
        System.out.println("Route: " + startPoint + " <-> " + endPoint);
        System.out.printf("Distance: %.2f km%n", distance);
        System.out.println("------------------------------------");
    } 
}
