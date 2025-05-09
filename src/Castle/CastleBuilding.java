package Castle;

import java.io.Serializable;
//для таверны и тд
public class CastleBuilding implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private int cost;
    
    public CastleBuilding(String name, String description, int cost) {
        this.name = name;
        this.description = description;
        this.cost = cost;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getCost() {
        return cost;
    }
} 