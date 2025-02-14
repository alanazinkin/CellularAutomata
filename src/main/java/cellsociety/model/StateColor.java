package cellsociety.model;

import java.lang.reflect.Field;
import javafx.scene.paint.Color;

public class StateColor {

  // generated using ChatGPT
  public String getColor(Color color) {
    for (Field field : Color.class.getFields()) {
      try {
        if (field.getType() == Color.class && field.get(null).equals(color)) {
          return field.getName().toLowerCase(); // Return the color name
        }
      } catch (IllegalAccessException ignored) {
      }
    }
    return color.toString(); // Default to rgba format if no name found
  }

}
