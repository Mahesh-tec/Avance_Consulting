import React, { useState } from 'react';
import axios from 'axios';

const App = () => {
  const [originalUrl, setOriginalUrl] = useState('');
  const [shortUrl, setShortUrl] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!originalUrl || !originalUrl.trim()) {
      setErrorMessage('Please enter a URL');
      setShortUrl('');
      return;
    }

    const urlPattern = /^(https?:\/\/)?([\w\d-]+\.){1,2}[a-z]{2,6}([\/\w .-]*)*\/?$/i;
    if (!urlPattern.test(originalUrl)) {
      setErrorMessage('Invalid URL format');
      setShortUrl('');
      return;
    }

    try {
      const response = await axios.post('http://localhost:8080/api/shorten', {
        originalUrl,
      });

      console.log('API response:', response.data);

      // ✅ Fixed: Use response data directly if it's already a string URL
      const shortLink = response.data;

      if (!shortLink || typeof shortLink !== 'string') {
        setErrorMessage('Invalid response from server');
        setShortUrl('');
        return;
      }

      setShortUrl(shortLink);
      setErrorMessage('');
    } catch (error) {
      console.error('Error:', error);
      setErrorMessage('Server error: Failed to shorten URL');
      setShortUrl('');
    }
  };

  return (
    <div style={{ padding: '2rem', textAlign: 'center' }}>
      <h1>URL Shortener</h1>

      <form onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Enter a URL"
          value={originalUrl}
          onChange={(e) => setOriginalUrl(e.target.value)}
          required
          style={{ padding: '0.5rem', width: '300px' }}
        />
        <button type="submit" style={{ marginLeft: '10px', padding: '0.5rem 1rem' }}>
          Shorten
        </button>
      </form>

      {errorMessage && (
        <div style={{ marginTop: '1rem', color: 'red' }}>
          <strong>{errorMessage}</strong>
        </div>
      )}

      {shortUrl && !errorMessage && (
        <div style={{ marginTop: '1.5rem', color: 'green' }}>
          <p>Shortened URL:</p>
          <a href={shortUrl} target="_blank" rel="noopener noreferrer">
            {shortUrl}
          </a>
        </div>
      )}
    </div>
  );
};

export default App;
