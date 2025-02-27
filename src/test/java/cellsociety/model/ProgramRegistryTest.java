package cellsociety.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cellsociety.model.state.CreatureState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link ProgramRegistry}.
 * Tests the functionality of the registry that manages creature programs.
 *
 * @author Tatum McKinnis
 */
public class ProgramRegistryTest {

  private ProgramRegistry registry;
  private CreatureProgram mockProgram;

  /**
   * Sets up the test environment before each test case.
   */
  @BeforeEach
  public void setUp() {
    registry = new ProgramRegistry();
    mockProgram = mock(CreatureProgram.class);
  }

  /**
   * Tests that registering a program works correctly.
   */
  @Test
  public void registerProgram_WithValidStateAndProgram_RegistersCorrectly() {
    registry.registerProgram(CreatureState.HUNTER, mockProgram);

    assertTrue(registry.hasProgram(CreatureState.HUNTER));
    assertSame(mockProgram, registry.getProgram(CreatureState.HUNTER));
  }

  /**
   * Tests that getting a program for a registered state returns the correct program.
   */
  @Test
  public void getProgram_WithRegisteredState_ReturnsCorrectProgram() {
    registry.registerProgram(CreatureState.HUNTER, mockProgram);

    CreatureProgram result = registry.getProgram(CreatureState.HUNTER);

    assertSame(mockProgram, result);
  }

  /**
   * Tests that getting a program for an unregistered state returns null.
   */
  @Test
  public void getProgram_WithUnregisteredState_ReturnsNull() {
    assertNull(registry.getProgram(CreatureState.WANDERER));
  }

  /**
   * Tests that checking for a registered program works correctly.
   */
  @Test
  public void hasProgram_WithRegisteredState_ReturnsTrue() {
    registry.registerProgram(CreatureState.HUNTER, mockProgram);

    assertTrue(registry.hasProgram(CreatureState.HUNTER));
  }

  /**
   * Tests that checking for an unregistered program works correctly.
   */
  @Test
  public void hasProgram_WithUnregisteredState_ReturnsFalse() {
    assertFalse(registry.hasProgram(CreatureState.WANDERER));
  }

  /**
   * Tests that overwriting a registered program works correctly.
   */
  @Test
  public void registerProgram_WithAlreadyRegisteredState_OverwritesExistingProgram() {
    CreatureProgram firstProgram = mock(CreatureProgram.class);
    CreatureProgram secondProgram = mock(CreatureProgram.class);

    registry.registerProgram(CreatureState.HUNTER, firstProgram);
    registry.registerProgram(CreatureState.HUNTER, secondProgram);

    assertSame(secondProgram, registry.getProgram(CreatureState.HUNTER));
  }

  /**
   * Tests that clearing the registry works correctly.
   */
  @Test
  public void clear_WithRegisteredPrograms_RemovesAllPrograms() {
    registry.registerProgram(CreatureState.HUNTER, mockProgram);
    registry.clear();

    assertFalse(registry.hasProgram(CreatureState.HUNTER));
    assertNull(registry.getProgram(CreatureState.HUNTER));
  }
}