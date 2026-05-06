import { useActionState, useEffect, useState } from 'react'
import { getSensors, recordReading } from '../api'
import ResponseDisplay from './ResponseDisplay'

const METRIC_CONSTRAINTS = {
  temperature: { min: -90,  max: 60,   unit: '°C'  },
  humidity:    { min: 0,    max: 100,  unit: '%'   },
  wind_speed:  { min: 0,    max: 500,  unit: 'km/h' },
  pressure:    { min: 300,  max: 1100, unit: 'hPa' },
}

const PRESET_METRICS = Object.keys(METRIC_CONSTRAINTS)

async function recordAction(prevState, formData) {
  const sensorId = formData.get('sensorId')
  const metricSelect = formData.get('metricSelect')
  const metric = metricSelect === '__custom__' ? formData.get('customMetric').trim() : metricSelect
  const value = parseFloat(formData.get('value'))

  const constraints = METRIC_CONSTRAINTS[metric]
  if (constraints && (value < constraints.min || value > constraints.max)) {
    return {
      data: null,
      error: { message: `${metric} must be between ${constraints.min} and ${constraints.max} ${constraints.unit}` },
    }
  }

  return recordReading(sensorId, { metric, value })
}

function sensorLabel(s) {
  const location = [s.country, s.city].filter(Boolean).join(', ')
  return location ? `${s.sensorId} (${location})` : s.sensorId
}

export default function RecordReading() {
  const [state, formAction, isPending] = useActionState(recordAction, { data: null, error: null })
  const [isCustom, setIsCustom] = useState(false)
  const [selectedMetric, setSelectedMetric] = useState(PRESET_METRICS[0])
  const [sensors, setSensors] = useState([])
  const [sensorsLoading, setSensorsLoading] = useState(true)

  useEffect(() => {
    getSensors().then(({ data }) => {
      setSensors(data ?? [])
      setSensorsLoading(false)
    })
  }, [])

  const constraints = METRIC_CONSTRAINTS[selectedMetric]

  function handleMetricChange(e) {
    const val = e.target.value
    setIsCustom(val === '__custom__')
    setSelectedMetric(val === '__custom__' ? null : val)
  }

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
          <label htmlFor="rec-sensor">Sensor *</label>
          {sensorsLoading ? (
            <select id="rec-sensor" name="sensorId" disabled>
              <option>Loading sensors…</option>
            </select>
          ) : sensors.length === 0 ? (
            <select id="rec-sensor" name="sensorId" disabled>
              <option>No sensors registered yet</option>
            </select>
          ) : (
            <select id="rec-sensor" name="sensorId" required>
              {sensors.map(s => (
                <option key={s.id} value={s.sensorId}>{sensorLabel(s)}</option>
              ))}
            </select>
          )}
        </div>

        <div className="field">
          <label htmlFor="rec-metric">Metric *</label>
          <select id="rec-metric" name="metricSelect" onChange={handleMetricChange}>
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
            min={constraints?.min}
            max={constraints?.max}
            placeholder={constraints ? `${constraints.min} to ${constraints.max}` : 'e.g. 0.0'}
          />
          {constraints && (
            <span className="hint">
              Valid range: {constraints.min} – {constraints.max} {constraints.unit}
            </span>
          )}
        </div>

        <button type="submit" disabled={isPending || sensors.length === 0} className="btn-primary">
          {isPending ? 'Recording…' : 'Record Reading'}
        </button>
      </form>

      <ResponseDisplay state={state} />
    </div>
  )
}
