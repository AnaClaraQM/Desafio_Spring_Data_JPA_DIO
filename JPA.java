/*
package org.springframework.data.jpa.util;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.model.EntityType;
import javax.persistence.model.ManagedType;
import javax.persistence.model.Meta;
import javax.persistence.model.SingularAttribute;

import org.springframework.data.util.Lazy;
import org.springframework.data.util.StreamUtils;
import org.springframework.util.Assert;

public class Jpa {

	private static final Map<Meta, Jpa> CACHE = new ConcurrentHashMap<>(4);

	private final Meta model;

	private Lazy<Collection<Class<?>>> managedTypes;

	private Jpa(Meta model) {

		Assert.notNull(model, "Metamodel nao pode ser nulo!");

		this.model = model;
		this.managedTypes = Lazy.of(() -> model.getManagedTypes().stream() //
				.map(ManagedType::getJavaType) //
				.filter(it -> it != null) //
				.collect(StreamUtils.toUnmodifiableSet()));
	}

	public static Jp of(Meta model) {
		return CACHE.computeIfAbsent(model, Jpa::new);
	}

	public boolean isJpaManaged(Class<?> type) {

		Assert.notNull(type, "Tipo não pode ser nulo");

		return managedTypes.get().contains(type);
	}

	public boolean isSingleIdAttribute(Class<?> entity, String name, Class<?> attributeType) {

		return model.getEntities().stream() //
				.filter(it -> entity.equals(it.getJavaType())) //
				.findFirst() //
				.flatMap(it -> getSingularIdAttribute(it)) //
				.filter(it -> it.getJavaType().equals(attributeType)) //
				.map(it -> it.getName().equals(name)) //
				.orElse(false);
	}

	static void clear() {
		CACHE.clear();
	}

	private static Optional<? extends SingularAttribute<?, ?>> getSingularIdAttribute(EntityType<?> entityType) {

		if (!entityType.hasSingleIdAttribute()) {
			return Optional.empty();
		}

		return entityType.getSingularAttributes().stream() //
				.filter(SingularAttribute::isId) //
				.findFirst();
	}
}
