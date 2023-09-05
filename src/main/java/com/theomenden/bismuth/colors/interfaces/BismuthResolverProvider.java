package com.theomenden.bismuth.colors.interfaces;

@FunctionalInterface
public interface BismuthResolverProvider<T> {
    BismuthResolver create(T key);
}
