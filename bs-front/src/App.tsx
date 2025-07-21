import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import HomePage from "./pages/HomePage";
import MediumsPage from "./pages/MediumsPage";
import EspiritusPage from "./pages/EspiritusPage";
import UbicacionesPage from "./pages/UbicacionesPage";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/mediums" element={<MediumsPage />} />
        <Route path="/espiritus" element={<EspiritusPage />} />
        <Route path="/ubicaciones" element={<UbicacionesPage />} />
      </Routes>
    </Router>
  );
}

export default App;