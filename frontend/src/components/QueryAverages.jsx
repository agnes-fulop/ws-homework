import { useActionState } from 'react'
import { queryAverages } from '../api'

const PRESET_METRICS = ['temperature', 'humidity', 'wind_speed', 'pressure']

async function queryAction(prevState, formData) {
  const sensorIdsRaw = formData.get('sensorIds').trim()
  const sensorIds = sensorIdsRaw
    ? sensorIdsRaw.split(',').map(s => s.trim()).filter(Boolean)
    : null

  const metrics = formData.getAll('metric')
  const customMetric = formData.get('customMetric').trim()
  if (customMetric) metrics.push(customMetric)

  if (metrics.length === 0) {
    return { data: null, error: { message: 'Select at least one metric.' } }
  }

  const from = formData.get('from') || undefined
  const to = formData.get('to') || undefined

  return queryAverages({ sensorIds, metrics, from, to })
}

export default function QueryAverages() {
  const [state, formAction, isPending] = useActionState(queryAction, { data: null, error: null })

  return (
    <div className="panel">
      <h2>Query Averages</h2>
      <p className="subtitle">Compute average metric values across sensors and a date range.</p>
      <div className="endpoint-badge">
        <span className="method">GET</span>
        <span>/api/readings/averages</span>
      </div>

      <form action={formAction} className="form">
        <div className="field">
          <label>Sensor IDs</label>
          <input
            name="sensorIds"
            placeholder="sensor-01, sensor-02  (blank = all sensors)"
          />
          <span className="hint">Comma-separated. Leave blank to query all sensors.</span>
        </div>

        <div className="field">
          <label>Metrics *</label>
          <div className="checkboxes">
            {PRESET_METRICS.map(m => (
              <label key={m} className="checkbox-pill">
                <input
                  type="checkbox"
                  name="metric"
                  value={m}
                  defaultChecked={m === 'temperature'}
                />
                {m}
              </label>
            ))}
          </div>
        </div>

        <div className="field">
          <label>Custom Metric</label>
          <input name="customMetric" placeholder="e.g. co2_level  (optional)" />
        </div>

        <div className="field-row">
          <div className="field">
            <label htmlFor="q-from">From</label>
            <input id="q-from" name="from" type="date" />
          </div>
          <div className="field">
            <label htmlFor="q-to">To</label>
            <input id="q-to" name="to" type="date" />
          </div>
        </div>
        <span className="hint" style={{ marginTop: '-12px' }}>
          Both dates optional — defaults to last 24 h. Max 31 days apart.
        </span>

        <button type="submit" disabled={isPending} className="btn-primary">
          {isPending ? 'Querying…' : 'Run Query'}
        </button>
      </form>

      {(state.data || state.error) && (
        <div className={`response ${state.error ? 'response-error' : 'response-success'}`}
          style={{ marginTop: 32, maxWidth: 520 }}>
          <div className="response-header">
            {state.error ? '✗ Error' : '✓ Results'}
          </div>

          {state.error && (
            <pre>{JSON.stringify(state.error, null, 2)}</pre>
          )}

          {state.data && (
            <>
              {state.data.averages?.length > 0 ? (
                <table className="averages-table">
                  <thead>
                    <tr>
                      <th>Metric</th>
                      <th>Average</th>
                    </tr>
                  </thead>
                  <tbody>
                    {state.data.averages.map(a => (
                      <tr key={a.metric}>
                        <td>{a.metric}</td>
                        <td>{typeof a.average === 'number' ? a.average.toFixed(4) : a.average}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <div className="empty-state">No readings matched the query criteria.</div>
              )}
              <div className="divider" />
              <pre>{JSON.stringify(state.data, null, 2)}</pre>
            </>
          )}
        </div>
      )}
    </div>
  )
}
