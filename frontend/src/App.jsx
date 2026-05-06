import { useState } from 'react'
import RegisterSensor from './components/RegisterSensor'
import RecordReading from './components/RecordReading'
import QueryAverages from './components/QueryAverages'

const TABS = [
  { id: 'register', label: 'Register Sensor', component: RegisterSensor },
  { id: 'reading', label: 'Record Reading', component: RecordReading },
  { id: 'query', label: 'Query Averages', component: QueryAverages },
]

export default function App() {
  const [activeTab, setActiveTab] = useState('register')
  const { component: ActiveComponent } = TABS.find(t => t.id === activeTab)

  return (
    <div className="app">
      <aside className="sidebar">
        <div className="sidebar-header">
          <div className="sidebar-logo">⚡</div>
          <h1>Sensor API</h1>
          <p>Demo Dashboard</p>
        </div>
        <nav>
          {TABS.map(tab => (
            <button
              key={tab.id}
              className={`nav-btn${activeTab === tab.id ? ' active' : ''}`}
              onClick={() => setActiveTab(tab.id)}
            >
              {tab.label}
            </button>
          ))}
        </nav>
        <div className="sidebar-footer">
          <span>Spring Boot API</span>
          <span className="port-badge">:8080</span>
        </div>
      </aside>
      <main className="content">
        <ActiveComponent />
      </main>
    </div>
  )
}
