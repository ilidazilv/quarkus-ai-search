import React, { useState, useEffect, useRef } from 'react';
import useWebSocket from 'react-use-websocket';

function ChatBot({ onPropertyResults }) {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [isOpen, setIsOpen] = useState(false);
    const messagesEndRef = useRef(null);
    const [processedMessageIds, setProcessedMessageIds] = useState(new Set());

    // Determine WebSocket URL
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const wsUrl = `${protocol}//localhost:8090/chatbot`;

    // Initialize WebSocket with manual control over connection
    const { sendMessage, lastMessage, readyState, getWebSocket } = useWebSocket(wsUrl, {
        onOpen: () => console.log('WebSocket connection established'),
        onError: (event) => console.error('WebSocket error:', event),
        shouldReconnect: (closeEvent) => true, // Auto reconnect
        reconnectAttempts: 10,
        reconnectInterval: 3000,
        share: true, // Ensure we only have one connection
        retryOnError: true,
    });

    // Handle incoming messages with duplicate prevention
    useEffect(() => {
        if (lastMessage && lastMessage.data) {
            try {
                const data = JSON.parse(lastMessage.data);

                // Generate a unique ID for this message based on its content
                const messageId = JSON.stringify(data);

                // Only process this message if we haven't seen it before
                if (!processedMessageIds.has(messageId)) {
                    // Add message to processed set
                    setProcessedMessageIds(prev => new Set(prev).add(messageId));

                    // Add bot message to chat
                    setMessages(prev => [...prev, {
                        text: data.message,
                        sender: 'bot',
                        timestamp: Date.now()
                    }]);

                    // If properties were returned, send them to the parent component
                    if (data.properties && data.properties.length > 0) {
                        onPropertyResults(data.properties);
                    }
                }
            } catch (error) {
                console.error('Error parsing WebSocket message:', error);
            }
        }
    }, [lastMessage, onPropertyResults, processedMessageIds]);

    // Cleanup old processed messages to prevent memory leaks
    useEffect(() => {
        const cleanupInterval = setInterval(() => {
            // Only keep messages from the last 5 minutes
            const fiveMinutesAgo = Date.now() - 5 * 60 * 1000;
            setMessages(prev => prev.filter(msg => !msg.timestamp || msg.timestamp > fiveMinutesAgo));
        }, 60000); // Run every minute

        return () => clearInterval(cleanupInterval);
    }, []);

    // Scroll to bottom when messages update
    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!input.trim()) return;

        // Add user message to chat
        setMessages(prev => [...prev, {
            text: input,
            sender: 'user',
            timestamp: Date.now()
        }]);

        // Send message to server
        sendMessage(input);

        // Clear input
        setInput('');
    };

    const toggleChat = () => {
        setIsOpen(!isOpen);
    };

    // Close chat completely (including WebSocket)
    const closeChat = () => {
        setIsOpen(false);
        // Optionally close the WebSocket connection
        const ws = getWebSocket();
        if (ws) {
            ws.close();
        }
    };

    // Connection status
    const connectionStatus = {
        [WebSocket.CONNECTING]: 'Connecting',
        [WebSocket.OPEN]: 'Open',
        [WebSocket.CLOSING]: 'Closing',
        [WebSocket.CLOSED]: 'Closed',
    }[readyState];

    return (
        <div className="fixed bottom-4 right-4 z-50">
            {/* Chat button */}
            <button
                onClick={toggleChat}
                className="bg-blue-600 hover:bg-blue-700 text-white rounded-full p-3 shadow-lg flex items-center justify-center"
            >
                {isOpen ? (
                    <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                    </svg>
                ) : (
                    <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
                    </svg>
                )}
            </button>

            {/* Chat window */}
            {isOpen && (
                <div className="absolute bottom-16 right-0 w-80 sm:w-96 bg-white rounded-lg shadow-xl overflow-hidden flex flex-col">
                    {/* Chat header */}
                    <div className="bg-blue-600 text-white px-4 py-3 flex justify-between items-center">
                        <div className="font-medium flex items-center">
                            <span>Property Assistant</span>
                            {connectionStatus !== 'Open' && (
                                <span className="ml-2 text-xs bg-yellow-500 text-white px-2 py-1 rounded-full">
                                    {connectionStatus}
                                </span>
                            )}
                        </div>
                        <div className="flex items-center">
                            <button
                                onClick={() => setMessages([])}
                                className="mr-2 text-white hover:text-gray-200 focus:outline-none"
                                title="Clear conversation"
                            >
                                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                </svg>
                            </button>
                            <button
                                onClick={closeChat}
                                className="focus:outline-none"
                                title="Close chat"
                            >
                                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                </svg>
                            </button>
                        </div>
                    </div>

                    {/* Chat messages */}
                    <div className="flex-1 p-4 overflow-y-auto max-h-96">
                        {messages.length === 0 ? (
                            <div className="text-center text-gray-500 py-8">
                                <p>Start a conversation with Bob!</p>
                            </div>
                        ) : (
                            messages.map((msg, index) => (
                                <div
                                    key={`${index}-${msg.timestamp || Date.now()}`}
                                    className={`mb-3 ${msg.sender === 'user' ? 'text-right' : 'text-left'}`}
                                >
                                    <div
                                        className={`inline-block px-4 py-2 rounded-lg ${
                                            msg.sender === 'user'
                                                ? 'bg-blue-600 text-white'
                                                : 'bg-gray-200 text-gray-800'
                                        }`}
                                    >
                                        {msg.text}
                                    </div>
                                </div>
                            ))
                        )}
                        <div ref={messagesEndRef} />
                    </div>

                    {/* Chat input */}
                    <form onSubmit={handleSubmit} className="border-t border-gray-200 p-3">
                        <div className="flex rounded-md shadow-sm">
                            <input
                                type="text"
                                value={input}
                                onChange={(e) => setInput(e.target.value)}
                                placeholder="Type your message..."
                                className="flex-1 min-w-0 block w-full px-3 py-2 rounded-l-md border border-r-0 focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                disabled={connectionStatus !== 'Open'}
                            />
                            <button
                                type="submit"
                                className={`inline-flex items-center px-3 py-2 border border-transparent text-sm font-medium rounded-r-md text-white ${
                                    connectionStatus === 'Open'
                                        ? 'bg-blue-600 hover:bg-blue-700'
                                        : 'bg-gray-400'
                                } focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500`}
                                disabled={connectionStatus !== 'Open'}
                            >
                                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
                                </svg>
                            </button>
                        </div>
                    </form>
                </div>
            )}
        </div>
    );
}

export default ChatBot;