import Home from './index.js'
import {render, screen} from './../test-utils.jsx'

describe('Pruebas pantalla de inicio', ()=>{

    test('El texto de inicio aparece en la pagina', async () =>{
        render(<Home />);
        // screen.debug();
        const titulo = screen.getByText(/7 Islas/);
        expect(titulo).toBeInTheDocument();
    });

}
);