import React from 'react';
import { ApolloProvider, ApolloClient, InMemoryCache } from '@apollo/client';
import PropertySearchPage from './PropertySearchPage';

// Configure Apollo Client
const client = new ApolloClient({
  uri: 'http://localhost:8090/graphql',
  cache: new InMemoryCache()
});

function App() {
  return (
      <ApolloProvider client={client}>
        <div className="min-h-screen bg-gray-50">
          <header className="bg-white shadow">
            <div className="max-w-7xl mx-auto py-6 px-4">
              <h1 className="text-3xl font-bold text-gray-900">Property Finder</h1>
            </div>
          </header>
          <main>
            <PropertySearchPage />
          </main>
        </div>
      </ApolloProvider>
  );
}

export default App;