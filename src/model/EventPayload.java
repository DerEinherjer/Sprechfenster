package model;

public class EventPayload
{

  public Type type;
  public Object obj;

  public enum Type
  {
    tournamentCreated,
    tournamentDeleted,
    tournamentStateChanged,
    fencerCreated,
    fencerDeleted,
    roundPreliminaryCreated,
    roundPreliminaryDeleted,
    roundFinalCreated,
    roundFinalDeleted,
    valueChanged
  }

  public EventPayload(Object obj, Type type)
  {
    this.obj = obj;
    this.type = type;
  }
}
