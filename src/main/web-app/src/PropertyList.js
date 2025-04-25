import React, { useState } from 'react';
import PropertyModal from './PropertyModal';

function PropertyList({ properties }) {
    const [selectedProperty, setSelectedProperty] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);

    const handleViewDetails = (property) => {
        setSelectedProperty(property);
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
    };

    return (
        <>
            <div className="mt-4 grid gap-6 lg:grid-cols-2">
                {properties.map(property => (
                    <div key={property.id} className="bg-white overflow-hidden shadow rounded-lg hover:shadow-md transition-shadow">
                        <div className="px-4 py-5 sm:p-6">
                            <h3 className="text-lg leading-6 font-medium text-gray-900 mb-2">
                                {property.title}
                            </h3>
                            <div className="mb-3 text-sm text-gray-500">{property.singleLine}</div>
                            <p className="mt-2 text-sm text-gray-700 line-clamp-3">
                                {property.description}
                            </p>
                            <div className="mt-4 flex justify-between items-center">
                                <span className="text-xs text-gray-500">ID: {property.id}</span>
                                <button
                                    onClick={() => handleViewDetails(property)}
                                    className="inline-flex items-center px-3 py-1 border border-transparent text-sm font-medium rounded-md text-blue-700 bg-blue-100 hover:bg-blue-200 focus:outline-none"
                                >
                                    View details
                                </button>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            {/* Property details modal */}
            <PropertyModal
                isOpen={isModalOpen}
                onClose={closeModal}
                property={selectedProperty}
            />
        </>
    );
}

export default PropertyList;