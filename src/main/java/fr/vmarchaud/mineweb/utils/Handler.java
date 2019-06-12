package fr.vmarchaud.mineweb.utils;

public interface Handler<R, E>
{
    R handle(final E p0);
}
