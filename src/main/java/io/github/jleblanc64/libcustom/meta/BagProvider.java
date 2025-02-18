package io.github.jleblanc64.libcustom.meta;

import java.util.Collection;

// session is of type org.hibernate.engine.spi.SharedSessionContractImplementor
public interface BagProvider<Bag> {
    Bag of(Object session);

    Bag of(Object session, Collection<?> collection);
}
