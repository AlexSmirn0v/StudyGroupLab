package model;

import java.io.Serializable;

import model.CommandMessage.Wrap;

public sealed interface Sendable extends Serializable permits StudyGroup, Wrap, UpdateRequest  {
}
