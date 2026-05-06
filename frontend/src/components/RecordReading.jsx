import { useActionState, useState } from 'react'
import { recordReading } from '../api'
import ResponseDisplay from './ResponseDisplay'

const PRESET_METRICS = ['temperature', 'humidity', 'wind_speed', 'pressure']

async function recordAction(prevState, formData) {
  const sensorId = formData.get('sensorId').trim()
  const metricSelect = formData.get('metricSelect')
  const metric = metricSelect === '__custom__' ? formData.get('customMetric').trim() : metricSelect
  const value = parseFloat(formData.get('value'))
  return recordReading(sensorId, { metric, value })
}

export default function RecordReading() {
  const [state, formAction, isPending] = useActionState(recordAction, { data: null, error: null })
  const [isCustom, setIsCustom] = useState(false)

  return (
    <div className="panel">
      <h2>Record Reading</h2>
      <p className="subtitle">Submit a metric value for a registered sensor.</p>
      <div className="endpoint-badge">
        <span className="method">POST</span>
        <span>/api/sensors/<em>&#123;sensorId&#125;</em>/readings</span>
      </div>

      <form action={formAction} className="form">
        <div className="field">
          <label htmlFor="rec-sensor">Sensor ID *</label>
          <input id="rec-sensor" name="sensorId" required placeholder="e.g. sensor-berlin-01" />
        </div>

        <div className="field">
          <label htmlFor="rec-metric">Metric *</label>
          <select
            id="rec-metric"
            name="metricSelect"
            onChange={e => setIsCustom(e.target.value === '__custom__')}
          >
            {PRESET_METRICS.map(m => (
              <option key={m} value={m}>{m}</option>
            ))}
            <option value="__custom__">Custom…</option>
          </select>
        </div>

        {isCustom && (
          <div className="field">
            <label htmlFor="rec-custom">Custom Metric Name *</label>
            <input id="rec-custom" name="customMetric" required placeholder="e.g. co2_level" />
          </div>
        )}

        <div className="field">
          <label htmlFor="rec-value">Value *</label>
          <input
            id="rec-value"
            name="value"
            type="number"
            step="any"
            required
            placeholder="e.g. 22.5"
          />
        </div>

        <button type="submit" disabled={isPending} className="btn-primary">
          {isPending ? 'Recording…' : 'Record Reading'}
        </button>
      </form>

      <ResponseDisplay state={state} />
    </div>
  )
}
