import React, { useState } from 'react';
import { gql, useLazyQuery } from '@apollo/client';
import PropertyList from './PropertyList';

const SEARCH_PROPERTIES = gql`
  query SearchProperties($searchTerm: String!) {
    search(search: $searchTerm) {
      id
      title
      description
      singleLine
    }
  }
`;

function PropertySearchPage() {
    const [searchTerm, setSearchTerm] = useState('');
    const [executeSearch, { loading, error, data }] = useLazyQuery(SEARCH_PROPERTIES);

    const handleSearch = () => {
        if (searchTerm.trim()) {
            executeSearch({ variables: { searchTerm } });
        }
    };

    return (
        <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
            <div className="px-4 py-6 sm:px-0">
                <div className="mb-6">
                    <div className="flex rounded-md shadow-sm">
                        <input
                            type="text"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            placeholder="Find properties... (e.g., 'apartments in Spain')"
                            className="flex-1 min-w-0 block w-full px-4 py-3 rounded-l-md border focus:ring-blue-500 focus:border-blue-500"
                            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                        />
                        <button
                            onClick={handleSearch}
                            className="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-r-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                            disabled={loading}
                        >
                            {loading ? 'Searching...' : 'Search'}
                        </button>
                    </div>
                    <p className="mt-2 text-sm text-gray-500">
                        Try natural language queries like "apartments near the beach" or "houses with garden in rural areas"
                    </p>
                </div>

                {error && (
                    <div className="bg-red-50 border-l-4 border-red-400 p-4 mb-4">
                        <div className="flex">
                            <div className="ml-3">
                                <p className="text-sm text-red-700">
                                    Error: {error.message}
                                </p>
                            </div>
                        </div>
                    </div>
                )}

                {data?.search && (
                    <PropertyList properties={data.search} />
                )}

                {loading && (
                    <div className="flex justify-center py-12">
                        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
                    </div>
                )}

                {data?.search?.length === 0 && !loading && (
                    <div className="text-center py-12">
                        <svg className="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                        <h3 className="mt-2 text-sm font-medium text-gray-900">No properties found</h3>
                        <p className="mt-1 text-sm text-gray-500">Try adjusting your search terms.</p>
                    </div>
                )}
            </div>
        </div>
    );
}

export default PropertySearchPage;