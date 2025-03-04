/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.results.graph;

import org.hibernate.metamodel.mapping.EntityVersionMapping;
import org.hibernate.spi.NavigablePath;
import org.hibernate.sql.results.graph.internal.ImmutableFetchList;
import org.hibernate.type.descriptor.java.JavaType;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractFetchParent implements FetchParent {
	private final FetchableContainer fetchContainer;
	private final NavigablePath navigablePath;

	private ImmutableFetchList fetches = ImmutableFetchList.EMPTY;
	private boolean hasJoinFetches;
	private boolean containsCollectionFetches;

	public AbstractFetchParent(FetchableContainer fetchContainer, NavigablePath navigablePath) {
		this.fetchContainer = fetchContainer;
		this.navigablePath = navigablePath;
	}

	public void afterInitialize(FetchParent fetchParent, DomainResultCreationState creationState) {
		assert fetches == ImmutableFetchList.EMPTY;
		resetFetches( creationState.visitFetches( fetchParent ) );
	}

	protected void resetFetches(ImmutableFetchList newFetches) {
		this.fetches = newFetches;
		this.hasJoinFetches = newFetches.hasJoinFetches();
		this.containsCollectionFetches = newFetches.containsCollectionFetches();
	}

	public FetchableContainer getFetchContainer() {
		return fetchContainer;
	}

	@Override
	public NavigablePath getNavigablePath() {
		return navigablePath;
	}

	@Override
	public JavaType<?> getResultJavaType() {
		return fetchContainer.getJavaType();
	}

	@Override
	public FetchableContainer getReferencedMappingContainer() {
		return fetchContainer;
	}

	@Override
	public ImmutableFetchList getFetches() {
		return fetches;
	}

	@Override
	public Fetch findFetch(final Fetchable fetchable) {
		if ( fetchable instanceof EntityVersionMapping ) {
			return fetches.get( ( (EntityVersionMapping) fetchable ).getVersionAttribute() );
		}
		return fetches.get( fetchable );
	}

	@Override
	public boolean hasJoinFetches() {
		return hasJoinFetches;
	}

	@Override
	public boolean containsCollectionFetches() {
		return containsCollectionFetches;
	}
}
