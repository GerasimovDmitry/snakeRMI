package sample.ModelsUI;

import sample.Models.Direction;

import java.io.Serializable;

public class StepParams implements Serializable {
    public Direction nextDirection;
    public Boolean isExit = false;
}
