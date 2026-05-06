async function fetchApi(url, options) {
  try {
    const res = await fetch(url, options)
    let json
    try {
      json = await res.json()
    } catch {
      json = { message: `HTTP ${res.status} ${res.statusText}` }
    }
    if (!res.ok) return { data: null, error: json }
    return { data: json, error: null }
  } catch (e) {
    return { data: null, error: { message: e.message } }
  }
}

export function getSensors() {
  return fetchApi('/api/sensors', { method: 'GET' })
}

export function registerSensor(body) {
  return fetchApi('/api/sensors', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  })
}

export function recordReading(sensorId, body) {
  return fetchApi(`/api/sensors/${encodeURIComponent(sensorId)}/readings`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  })
}

export function queryAverages({ sensorIds, metrics, from, to }) {
  const params = new URLSearchParams()
  if (sensorIds) sensorIds.forEach(id => params.append('sensorIds', id))
  metrics.forEach(m => params.append('metrics', m))
  if (from) params.append('from', from)
  if (to) params.append('to', to)
  return fetchApi(`/api/readings/averages?${params}`, { method: 'GET' })
}
