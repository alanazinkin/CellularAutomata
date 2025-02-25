package cellsociety.controller;

import cellsociety.model.Grid;
import cellsociety.model.Simulation;
import cellsociety.model.simulations.*;

import java.lang.reflect.Constructor;
import java.util.Map;

public class SimulationFactory {
    public static Simulation createSimulation(String type, SimulationConfig config, Grid grid) {

        SimulationController.SimulationType simType = SimulationController.SimulationType.fromString(type)
                .orElseThrow(() -> new IllegalArgumentException("Invalid simulation type: " + type));

        Map<String, Double> parameters = config.getParameters();


        try {
            return switch (simType) {
                case GAME_OF_LIFE -> new GameOfLife(config, grid);
                case SPREADING_FIRE -> new Fire(config, grid,
                        parameters.get("fireProb"),
                        parameters.get("treeProb"));
                case PERCOLATION -> createViaReflection(
                        "Percolation",
                        new Class<?>[]{SimulationConfig.class, Grid.class, double.class},
                        new Object[]{config, grid, parameters.get("percolationProb")});
                case SCHELLING -> createViaReflection(
                        "Schelling",
                        new Class<?>[]{SimulationConfig.class, Grid.class, double.class},
                        new Object[]{config, grid, parameters.get("satisfaction")});
                case WATOR_WORLD -> createViaReflection(
                        "WaTorWorld",
                        new Class<?>[]{SimulationConfig.class, Grid.class, double.class, double.class, double.class, double.class},
                        new Object[]{config, grid,
                                parameters.get("fishBreedTime"),
                                parameters.get("sharkBreedTime"),
                                parameters.get("sharkInitialEnergy"),
                                parameters.get("sharkEnergyGain")});
                case LANGTON_LOOP -> new LangtonLoop(config, grid);
                case SUGAR_SCAPE -> new SugarScape(config, grid);
                case BACTERIA -> new BacteriaColoniesSimulation(config, grid);
                case ANT -> new AntSimulation(config, grid);
                case TEMPESTI_LOOP -> new TempestiLoop(config, grid);
                case RULES_GAME_OF_LIFE -> new RuleBasedGameOfLife(config, grid);
                default -> throw new IllegalArgumentException("Unsupported simulation type: " + simType);
            };
        } catch (Exception e) {
            throw new RuntimeException("Failed to create simulation of type '" + type +
                    "'. Error: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a simulation instance using reflection with explicit package search
     */
    private static Simulation createViaReflection(String className, Class<?>[] paramTypes, Object[] params)
            throws ReflectiveOperationException {

        String[] potentialPackages = {
                "", // No package (default package)
                "simulations.",
                SimulationFactory.class.getPackage().getName() + ".",
                "model.simulations.",
                "model."
        };

        for (String pkg : potentialPackages) {
            try {
                String fullClassName = pkg + className;
                Class<?> clazz = Class.forName(fullClassName);

                try {
                    Constructor<?> constructor = clazz.getConstructor(paramTypes);
                    return (Simulation) constructor.newInstance(params);
                } catch (NoSuchMethodException e) {
                    tryWithAlternativeTypes(clazz, paramTypes, params);
                }
            } catch (ClassNotFoundException e) {
                continue;
            }
        }

        throw new ClassNotFoundException("Could not find simulation class: " + className);
    }

    /**
     * Try to find a matching constructor by converting between primitive and wrapper types
     */
    private static Simulation tryWithAlternativeTypes(Class<?> clazz, Class<?>[] paramTypes, Object[] params)
            throws ReflectiveOperationException {

        Constructor<?>[] constructors = clazz.getConstructors();

        for (Constructor<?> constructor : constructors) {
            Class<?>[] ctorParamTypes = constructor.getParameterTypes();

            // Check if this constructor has the right number of parameters
            if (ctorParamTypes.length == paramTypes.length) {
                boolean matches = true;

                // Check if each parameter is compatible
                for (int i = 0; i < ctorParamTypes.length; i++) {
                    Class<?> ctorType = ctorParamTypes[i];
                    Class<?> paramType = paramTypes[i];

                    // Check for direct match or primitive/wrapper match
                    if (!ctorType.isAssignableFrom(paramType) &&
                            !isPrimitiveWrapperMatch(ctorType, paramType)) {
                        matches = false;
                        break;
                    }
                }

                // If all parameters match, use this constructor
                if (matches) {
                    return (Simulation) constructor.newInstance(params);
                }
            }
        }

        throw new NoSuchMethodException("No suitable constructor found");
    }

    /**
     * Check if two types match considering primitive-wrapper conversions
     */
    private static boolean isPrimitiveWrapperMatch(Class<?> type1, Class<?> type2) {
        // Check primitive double and Double wrapper
        if ((type1 == double.class && type2 == Double.class) ||
                (type1 == Double.class && type2 == double.class)) {
            return true;
        }

        // Check primitive int and Integer wrapper
        if ((type1 == int.class && type2 == Integer.class) ||
                (type1 == Integer.class && type2 == int.class)) {
            return true;
        }

        // Add other primitive/wrapper pairs as needed

        return false;
    }
}