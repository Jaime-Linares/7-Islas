import React from 'react';
import reglas from '../static/images/RulesRecortada.png'
import '../static/css/rules/rules.css'


export default function Rules() {
    return (
        <div className='image-container'>
            <img src={reglas} alt="Reglas de 7islas" />
        </div>
    );
}