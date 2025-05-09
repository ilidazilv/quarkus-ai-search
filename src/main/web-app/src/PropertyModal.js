import React, { useState } from 'react';

function PropertyModal({ isOpen, onClose, property }) {
    const [activeImageIndex, setActiveImageIndex] = useState(0);

    if (!isOpen) return null;

    // Generate media URL from media ID
    const getMediaUrl = (mediaId) => {
        return `https://live-file-api.igluu.cz/file/${mediaId}?isPublic=true`;
    };

    // Handle next image click
    const handleNextImage = () => {
        if (property?.media && property.media.length > 0) {
            setActiveImageIndex((prev) => (prev + 1) % property.media.length);
        }
    };

    // Handle previous image click
    const handlePrevImage = () => {
        if (property?.media && property.media.length > 0) {
            setActiveImageIndex((prev) => (prev - 1 + property.media.length) % property.media.length);
        }
    };

    return (
        <div className="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-hidden flex flex-col">
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
                                {/* Media gallery */}
                                {property.media && property.media.length > 0 && (
                                    <div className="mb-6">
                                        <div className="relative aspect-w-16 aspect-h-9 bg-gray-100 rounded-lg overflow-hidden">
                                            <img
                                                src={getMediaUrl(property.media[activeImageIndex].id)}
                                                alt={`Property image ${activeImageIndex + 1}`}
                                                className="object-cover w-full h-full"
                                            />

                                            {/* Navigation arrows */}
                                            {property.media.length > 1 && (
                                                <>
                                                    <button
                                                        onClick={handlePrevImage}
                                                        className="absolute left-2 top-1/2 transform -translate-y-1/2 bg-white bg-opacity-80 rounded-full p-2 shadow hover:bg-opacity-100 focus:outline-none"
                                                    >
                                                        <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                                                        </svg>
                                                    </button>
                                                    <button
                                                        onClick={handleNextImage}
                                                        className="absolute right-2 top-1/2 transform -translate-y-1/2 bg-white bg-opacity-80 rounded-full p-2 shadow hover:bg-opacity-100 focus:outline-none"
                                                    >
                                                        <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                                                        </svg>
                                                    </button>
                                                </>
                                            )}
                                        </div>

                                        {/* Thumbnails */}
                                        {property.media.length > 1 && (
                                            <div className="mt-2 flex space-x-2 overflow-x-auto pb-2">
                                                {property.media.map((mediaItem, index) => (
                                                    <button
                                                        key={mediaItem.id}
                                                        onClick={() => setActiveImageIndex(index)}
                                                        className={`flex-shrink-0 w-20 h-20 rounded-md overflow-hidden border-2 focus:outline-none ${
                                                            index === activeImageIndex ? 'border-blue-500' : 'border-transparent'
                                                        }`}
                                                    >
                                                        <img
                                                            src={getMediaUrl(mediaItem.id)}
                                                            alt={`Property thumbnail ${index + 1}`}
                                                            className="object-cover w-full h-full"
                                                        />
                                                    </button>
                                                ))}
                                            </div>
                                        )}
                                    </div>
                                )}

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