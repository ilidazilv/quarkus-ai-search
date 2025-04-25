import React from 'react';

function PropertyModal({ isOpen, onClose, property }) {
    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg shadow-xl max-w-3xl w-full max-h-[90vh] overflow-hidden flex flex-col">
                {/* Modal header */}
                <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
                    <h2 className="text-xl font-semibold text-gray-800">Property Details</h2>
                    <button
                        onClick={onClose}
                        className="text-gray-400 hover:text-gray-500 focus:outline-none"
                    >
                        <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                </div>

                {/* Modal content */}
                <div className="flex-1 overflow-y-auto p-6">
                    {
                        property ? (
                            <div>
                                <h1 className="text-2xl font-bold text-gray-900 mb-2">{property.title}</h1>
                                <p className="text-gray-600 mb-6">{property.singleLine}</p>

                                <h3 className="text-lg font-medium text-gray-900 mb-2">Description</h3>
                                <p className="text-gray-700 mb-6">{property.description}</p>

                                <div className="text-sm text-gray-500">
                                    Property ID: {property.id}
                                </div>
                            </div>
                        ) : (
                            <p>Property not found</p>
                        )
                    }
                </div>

                {/* Modal footer */}
                <div className="px-6 py-4 bg-gray-50 border-t border-gray-200">
                    <button
                        onClick={onClose}
                        className="w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                    >
                        Close
                    </button>
                </div>
            </div>
        </div>
    );
}

export default PropertyModal;