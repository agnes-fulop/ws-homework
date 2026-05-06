import { useActionState } from 'react'
import { registerSensor } from '../api'
import ResponseDisplay from './ResponseDisplay'

async function registerAction(prevState, formData) {
  const body = { sensorId: formData.get('sensorId') }
  const country = formData.get('country').trim()
  const city = formData.get('city').trim()
  if (country) body.country = country
  if (city) body.city = city
  return registerSensor(body)
}

export default function RegisterSensor() {
  const [state, formAction, isPending] = useActionState(registerAction, { data: null, error: null })

  return (
    <div className="panel">
      <h2>Register Sensor</h2>
      <p className="subtitle">Create a new sensor with an ID and optional location.</p>
      <div className="endpoint-badge">
        <span className="method">POST</span>
        <span>/api/sensors</span>
      </div>

      <form action={formAction} className="form">
        <div className="field">
          <label htmlFor="reg-id">Sensor ID *</label>
          <input id="reg-id" name="sensorId" required placeholder="e.g. sensor-berlin-01" />
        </div>

        <div className="field-row">
          <div className="field">
            <label htmlFor="reg-country">Country</label>
            <input id="reg-country" name="country" placeholder="e.g. Germany" />
          </div>
          <div className="field">
            <label htmlFor="reg-city">City</label>
            <input id="reg-city" name="city" placeholder="e.g. Berlin" />
          </div>
        </div>

        <button type="submit" disabled={isPending} className="btn-primary">
          {isPending ? 'Registering…' : 'Register Sensor'}
        </button>
      </form>

      <ResponseDisplay state={state} />
    </div>
  )
}
