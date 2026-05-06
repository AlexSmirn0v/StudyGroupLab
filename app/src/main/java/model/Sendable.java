package model;

import java.io.Serializable;

import model.CommandMessage.Wrap;

/**
 * Интерфейс для объектов, которые можно отправлять между клиентом и сервером.
 */
public sealed interface Sendable extends Serializable permits StudyGroup, Wrap, UpdateRequest  {
}
