import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './main.css'

function getRoot() {
    const root = document.getElementById('root');
    if (!root) {
        throw new Error('Not found #root component');
    }
    root.style.height = '100%';
    return root;
}
const root = ReactDOM.createRoot(getRoot());

root.render(
    <React.StrictMode>
        <App />
    </React.StrictMode>,
);
